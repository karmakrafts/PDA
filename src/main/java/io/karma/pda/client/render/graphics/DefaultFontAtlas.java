/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

import io.karma.pda.api.client.render.graphics.FontAtlas;
import io.karma.pda.api.client.render.graphics.GlyphSprite;
import io.karma.pda.api.common.app.theme.font.Font;
import io.karma.pda.api.common.util.Exceptions;
import io.karma.pda.common.PDAMod;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMaps;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.chars.CharSet;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTVertex;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.msdfgen.MSDFGen;
import org.lwjgl.util.msdfgen.MSDFGenBitmap;
import org.lwjgl.util.msdfgen.MSDFGenTransform;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Alexander Hinze
 * @since 04/05/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultFontAtlas implements FontAtlas {
    private static final int SPRITE_SIZE = 16;
    private final Font font;
    private final int sizeInSlots;
    private final DefaultGlyphSprite missingGlyphSprite;
    private final Char2ObjectOpenHashMap<GlyphSprite> glyphSprites = new Char2ObjectOpenHashMap<>();
    private final AtomicBoolean isReady = new AtomicBoolean(false);
    private int textureId;

    public DefaultFontAtlas(final Font font) {
        this.font = font;

        // Simple way of finding a size that fits all characters but is a power of 2
        final var numChars = font.getSupportedChars().toSet().size();
        int size = 2;
        while ((size * size) < numChars) {
            size <<= 1;
        }
        this.sizeInSlots = size;

        missingGlyphSprite = new DefaultGlyphSprite(SPRITE_SIZE, SPRITE_SIZE, 0F, 0F, 0F, 0F);
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

    private static long makeShape(final STBTTVertex.Buffer data) {
        try (final var stack = MemoryStack.stackPush()) {
            final var shapeAddress = stack.mallocPointer(1);
            if (MSDFGen.msdf_shape_alloc(shapeAddress) != 0) {
                throw new OutOfMemoryError("Could not allocate shape object");
            }
            final var shape = shapeAddress.get();
            if (shape == MemoryUtil.NULL) {
                throw new NullPointerException();
            }
            final var contourAddress = stack.mallocPointer(1);
            MSDFGen.msdf_shape_add_contour(shape, contourAddress);
            final var contour = contourAddress.get();
            if(contour == MemoryUtil.NULL) {
                throw new NullPointerException();
            }
            for (final var vertex : data) {
                try(final var innerStack = MemoryStack.stackPush()) {
                    final var segmentAddress = innerStack.mallocPointer(1);
                    MSDFGen.msdf_contour_add_edge(contour, segmentAddress);
                    final var segmentType = switch(vertex.type()) {
                        case STBTruetype.STBTT_vcurve -> 1;
                        case STBTruetype.STBTT_vcubic -> 2;
                        default -> 0;
                    };
                }
            }
            return shape;
        }
    }

    void rebuild() {
        isReady.set(false);
        PDAMod.EXECUTOR_SERVICE.submit(() -> {
            final var fontLocation = font.getLocation();
            PDAMod.LOGGER.debug("Rebuilding font atlas for font {} with {}x{} slots",
                fontLocation,
                sizeInSlots,
                sizeInSlots);
            synchronized (this) {
                glyphSprites.clear();
            }
            final var resourceManager = Minecraft.getInstance().getResourceManager();
            try (final var stream = resourceManager.getResourceOrThrow(fontLocation).open()) {
                final var data = stream.readAllBytes();
                final var buffer = ByteBuffer.allocateDirect(data.length).order(ByteOrder.nativeOrder());
                buffer.put(data);
                buffer.flip();
                try (final var stack = MemoryStack.stackPush()) {
                    final var fontInfo = STBTTFontinfo.malloc(stack);
                    if (!STBTruetype.stbtt_InitFont(fontInfo, buffer)) {
                        throw new IOException("Could not init font info");
                    }
                    font.getSupportedChars().forEachChar(c -> {
                        final var glyphIndex = STBTruetype.stbtt_FindGlyphIndex(fontInfo, c);
                        if (STBTruetype.stbtt_IsGlyphEmpty(fontInfo, glyphIndex)) {
                            return; // We are not interested in empty glyphs
                        }
                        final var shape = makeShape(STBTruetype.stbtt_GetGlyphShape(fontInfo, glyphIndex));
                        try (final var innerStack = MemoryStack.stackPush()) {
                            final var bitmap = MSDFGenBitmap.malloc(1, innerStack);
                            final var transform = MSDFGenTransform.calloc(1, innerStack);
                        }
                        MSDFGen.msdf_shape_free(shape);
                    });
                }
            }
            catch (Throwable error) {
                PDAMod.LOGGER.error("Could not rebuild font atlas for font {}: {}",
                    fontLocation,
                    Exceptions.toFancyString(error));
            }

            isReady.set(true); // We are done processing
        });
    }

    @Override
    public int getWidth() {
        return sizeInSlots * SPRITE_SIZE;
    }

    @Override
    public int getHeight() {
        return sizeInSlots * SPRITE_SIZE;
    }

    @Override
    public CharSet getSupportedChars() {
        return font.getSupportedChars().toSet();
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

    @Override
    public boolean isReady() {
        return isReady.get();
    }
}
