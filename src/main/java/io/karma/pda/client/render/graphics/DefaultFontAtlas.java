/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

import io.karma.pda.api.client.render.graphics.FontAtlas;
import io.karma.pda.api.client.render.graphics.GlyphSprite;
import io.karma.pda.api.common.app.theme.font.Font;
import io.karma.pda.api.common.util.Exceptions;
import io.karma.pda.client.util.TextureUtils;
import io.karma.pda.common.PDAMod;
import it.unimi.dsi.fastutil.chars.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLLoader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.msdfgen.MSDFGen;
import org.lwjgl.util.msdfgen.MSDFGenBounds;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Alexander Hinze
 * @since 04/05/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultFontAtlas implements FontAtlas {
    private final Font font;
    private final int spriteSize;
    private final int sizeInSlots;
    private final int spriteBorder;
    private final double sdfRange;
    private final int renderType;
    private final DefaultGlyphSprite missingGlyphSprite;
    private final Char2ObjectOpenHashMap<GlyphSprite> glyphSprites = new Char2ObjectOpenHashMap<>();
    private final AtomicBoolean isReady = new AtomicBoolean(false);
    private int textureId;

    public DefaultFontAtlas(final Font font, final int spriteSize, final int spriteBorder, final double sdfRange,
                            final int renderType) {
        this.font = font;
        this.spriteSize = spriteSize;
        this.spriteBorder = spriteBorder;
        this.sdfRange = sdfRange;
        this.renderType = renderType;

        // Simple way of finding a size that fits all characters but is a power of 2
        final var maxSize = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE) / spriteSize;
        final var numChars = font.getSupportedChars().toSet().size();
        int size = 2;
        while ((size * size) < numChars) {
            if (size > maxSize) {
                throw new IllegalStateException("Font atlas too large, not supported yet");
            }
            size <<= 1;
        }
        this.sizeInSlots = size;

        // @formatter:off
        missingGlyphSprite = new DefaultGlyphSprite(new DefaultGlyphMetrics(spriteSize, spriteSize, 0, 0, 0, spriteSize, 0),
            spriteSize, spriteSize, 0F, 0F, 0F, 0F);
        // @formatter:on
        textureId = TextureUtils.createTexture();
        rebuild();
        PDAMod.DISPOSITION_HANDLER.addObject(this);
    }

    private static void dump(final BufferedImage image, final ResourceLocation location) throws IOException {
        final var directory = FMLLoader.getGamePath().resolve("pda");
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        final var fileName = String.format("%s_%s.png", location.getNamespace(), location.getPath().replace('/', '_'));
        final var filePath = directory.resolve(fileName);
        Files.deleteIfExists(filePath);
        try (final var outStream = Files.newOutputStream(filePath)) {
            ImageIO.write(image, "PNG", outStream);
        }
        PDAMod.LOGGER.debug("Dumped font atlas for {} to {}", location, filePath);
    }

    // Auto-framing algorithm partially adapted from https://github.com/Chlumsky/msdfgen/blob/master/main.cpp
    private void rebuildBlocking() {
        final var fontLocation = font.getLocation();
        PDAMod.LOGGER.debug("Rebuilding font atlas for font {} with {}x{} slots",
            fontLocation,
            sizeInSlots,
            sizeInSlots);
        synchronized (this) {
            glyphSprites.clear();
        }
        final var resourceManager = Minecraft.getInstance().getResourceManager();
        try (final var fontShapes = new MSDFFont(resourceManager.getResourceOrThrow(fontLocation).open())) {
            final var stack = MemoryStack.stackGet();
            final var previousSP = stack.getPointer();

            final var chars = font.getSupportedChars().toArray();
            final var atlasImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            final var shapes = new Char2LongLinkedOpenHashMap();
            final var boundsBuffer = MSDFGenBounds.malloc(1, stack);
            var maxWidth = 0.0;
            var maxHeight = 0.0;
            // Extract vector shape for every glyph and determine common scaling factor
            for (final var c : chars) {
                final var shape = fontShapes.createGlyphShape(c);
                boundsBuffer.rewind();
                MSDFGenUtil.throwIfError(MSDFGen.msdf_shape_get_bounds(shape, boundsBuffer));
                final var width = boundsBuffer.r() - boundsBuffer.l();
                if (maxWidth < width) {
                    maxWidth = width;
                }
                final var height = boundsBuffer.t() - boundsBuffer.b();
                if (maxHeight < height) {
                    maxHeight = height;
                }
                shapes.put(c, shape);
            }
            final var scale = (double) spriteSize / Math.max(maxWidth, maxHeight);
            // Render glyphs to atlas image
            var index = 0;
            for (final var entry : shapes.char2LongEntrySet()) {
                final var shape = entry.getLongValue();
                MSDFGenUtil.scaleShape(shape, scale); // Scale to default size of font
                MSDFGenUtil.throwIfError(MSDFGen.msdf_shape_edge_colors_simple(shape, 3.0));
                boundsBuffer.rewind();
                MSDFGenUtil.throwIfError(MSDFGen.msdf_shape_get_bounds(shape, boundsBuffer));
                final var tx = -boundsBuffer.l();
                final var ty = -boundsBuffer.b() + (spriteSize - (boundsBuffer.t() - boundsBuffer.b()));
                MSDFGenUtil.renderShapeToImage(renderType,
                    spriteSize,
                    spriteSize,
                    shape,
                    1.0,
                    1.0,
                    tx,
                    ty,
                    sdfRange,
                    atlasImage,
                    (index % sizeInSlots) * spriteSize,
                    (index / sizeInSlots) * spriteSize);
                MSDFGen.msdf_shape_free(shape);
                index++;
            }
            dump(atlasImage, fontLocation);

            stack.setPointer(previousSP);
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error("Could not rebuild font atlas for font {}: {}",
                fontLocation,
                Exceptions.toFancyString(error));
        }
    }

    void rebuild() {
        isReady.set(false);
        PDAMod.EXECUTOR_SERVICE.submit(() -> {
            rebuildBlocking();
            isReady.set(true);
        });
    }

    @Override
    public int getWidth() {
        return sizeInSlots * spriteSize;
    }

    @Override
    public int getHeight() {
        return sizeInSlots * spriteSize;
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
