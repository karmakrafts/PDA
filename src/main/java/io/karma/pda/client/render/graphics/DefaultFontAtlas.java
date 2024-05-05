/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

import io.karma.pda.api.client.render.graphics.FontAtlas;
import io.karma.pda.api.client.render.graphics.GlyphSprite;
import io.karma.pda.api.common.app.theme.font.Font;
import io.karma.pda.api.common.util.Exceptions;
import io.karma.pda.common.PDAMod;
import it.unimi.dsi.fastutil.chars.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author Alexander Hinze
 * @since 04/05/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultFontAtlas implements FontAtlas {
    private final Font font;
    private final DefaultGlyphSprite missingGlyphSprite;
    private final CharOpenHashSet supportedChars = new CharOpenHashSet();
    private final Char2ObjectOpenHashMap<GlyphSprite> glyphSprites = new Char2ObjectOpenHashMap<>();
    private int textureId;

    public DefaultFontAtlas(final Font font) {
        this.font = font;
        missingGlyphSprite = new DefaultGlyphSprite((int) font.getSize(), (int) font.getSize(), 0F, 0F, 0F, 0F);
        textureId = GL11.glGenTextures();
        if (textureId == -1) {
            throw new IllegalStateException("Could not allocate font atlas texture");
        }
        bind();
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        unbind();
        rebuild();
        PDAMod.DISPOSITION_HANDLER.addObject(this);
    }

    void rebuild() {
        PDAMod.EXECUTOR_SERVICE.submit(() -> {
            final var fontLocation = font.getLocation();
            PDAMod.LOGGER.debug("Rebuilding font atlas for font {}", fontLocation);
            synchronized (this) {
                supportedChars.clear();
                glyphSprites.clear();
            }
            final var resourceManager = Minecraft.getInstance().getResourceManager();

            try (final var stream = resourceManager.getResourceOrThrow(fontLocation).open()) {
                final var data = stream.readAllBytes();
                final var buffer = ByteBuffer.allocateDirect(data.length);
                buffer.put(data);
                buffer.flip();
                try (final var stack = MemoryStack.stackPush()) {
                    final var fontInfo = STBTTFontinfo.malloc(stack);
                    if (!STBTruetype.stbtt_InitFont(fontInfo, buffer)) {
                        throw new IOException("Could not init font info");
                    }
                }
            }
            catch (Throwable error) {
                PDAMod.LOGGER.error("Could not rebuild font atlas for font {}: {}",
                    fontLocation,
                    Exceptions.toFancyString(error));
            }
        });
    }

    @Override
    public synchronized CharSet getSupportedChars() {
        return CharSets.unmodifiable(supportedChars);
    }

    @Override
    public void dispose() {
        GL11.glDeleteTextures(textureId);
        textureId = -1;
    }

    @Override
    public int getTextureId() {
        return textureId;
    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public synchronized GlyphSprite getGlyphSprite(final char c) {
        return glyphSprites.getOrDefault(c, missingGlyphSprite);
    }

    @Override
    public synchronized Char2ObjectMap<GlyphSprite> getGlyphSprites() {
        return Char2ObjectMaps.unmodifiable(glyphSprites);
    }

    @Override
    public void bind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
    }

    @Override
    public void unbind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }
}
