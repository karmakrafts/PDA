/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

import com.mojang.blaze3d.systems.RenderSystem;
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
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.msdfgen.MSDFGen;
import org.lwjgl.util.msdfgen.MSDFGenBounds;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.util.Objects;
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
    private final float uScale;
    private final float vScale;
    private final DefaultGlyphSprite missingGlyphSprite;
    private final Char2ObjectOpenHashMap<DefaultGlyphSprite> glyphSprites = new Char2ObjectOpenHashMap<>();
    private final AtomicBoolean isReady = new AtomicBoolean(false);
    private final int textureId;
    private final Image missingGlyphImage;
    private int maxGlyphWidth;
    private int maxGlyphHeight;
    private int maxGlyphBearingX;
    private int maxGlyphBearingY;

    public DefaultFontAtlas(final Font font, final int spriteSize, final int spriteBorder, final double sdfRange,
                            final int renderType) {
        this.font = font;
        this.spriteSize = spriteSize;
        this.spriteBorder = spriteBorder;
        this.sdfRange = sdfRange;
        this.renderType = renderType;

        // Set up missing glyph image
        final var missingGlyphImage = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
        missingGlyphImage.setRGB(0, 0, 0xFF7700FF);
        missingGlyphImage.setRGB(1, 0, 0xFFFF00FF);
        missingGlyphImage.setRGB(1, 1, 0xFF7700FF);
        missingGlyphImage.setRGB(0, 1, 0xFFFF00FF);
        this.missingGlyphImage = missingGlyphImage.getScaledInstance(spriteSize,
            spriteSize,
            BufferedImage.SCALE_REPLICATE);

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
        uScale = 1F / getWidth();
        vScale = 1F / getHeight();

        // @formatter:off
        missingGlyphSprite = new DefaultGlyphSprite(new DefaultGlyphMetrics(spriteSize, spriteSize, 0, 0, 0, spriteSize, 0),
            spriteSize, spriteSize, 0F, 0F);
        // @formatter:on
        textureId = TextureUtils.createTexture();
        rebuild();
        PDAMod.DISPOSITION_HANDLER.addObject(this);
    }

    private void uploadTexture(final BufferedImage image) {
        bind();
        final var w = image.getWidth();
        final var h = image.getHeight();
        final var buffer = TextureUtils.toBuffer(image);
        try(final var stack = MemoryStack.stackPush()) {
            GL11.glTexParameteriv(GL11.GL_TEXTURE_2D, GL33.GL_TEXTURE_SWIZZLE_RGBA, stack.ints(
                GL11.GL_BLUE, GL11.GL_GREEN, GL11.GL_RED, GL11.GL_ALPHA));
        }
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, w, h, 0, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, buffer);
        unbind();
        isReady.set(true); // After uploading, we are ready
    }

    private static void dump(final BufferedImage image, final ResourceLocation location) {
        try {
            final var directory = FMLLoader.getGamePath().resolve("pda");
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            final var fileName = String.format("%s_%s.png",
                location.getNamespace(),
                location.getPath().replace('/', '_'));
            final var filePath = directory.resolve(fileName);
            Files.deleteIfExists(filePath);
            try (final var outStream = Files.newOutputStream(filePath)) {
                ImageIO.write(image, "PNG", outStream);
            }
            PDAMod.LOGGER.debug("Dumped font atlas for {} to {}", location, filePath);
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error("Could not dump font atlas {}: {}", location, Exceptions.toFancyString(error));
        }
    }

    private void rebuildBlocking() {
        final var fontLocation = font.getLocation();
        PDAMod.LOGGER.debug("Rebuilding font atlas for font {} with {}x{} slots",
            fontLocation,
            sizeInSlots,
            sizeInSlots);
        synchronized (this) {
            glyphSprites.clear();
        }

        final var atlasWidth = getWidth();
        final var atlasHeight = getHeight();
        final var atlasImage = new BufferedImage(atlasWidth, atlasHeight, BufferedImage.TYPE_INT_ARGB);
        final var atlasGraphics = atlasImage.createGraphics();
        final var resourceManager = Minecraft.getInstance().getResourceManager();
        final var shapes = new Char2LongLinkedOpenHashMap();

        var maxWidth = 0.0;
        var maxHeight = 0.0;

        try (final var fontShapes = new MSDFFont(resourceManager.getResourceOrThrow(fontLocation).open())) {
            final var chars = font.getSupportedChars().toArray();
            final var numChars = chars.length;
            // Extract vector shape for every glyph and determine common scaling factor
            for (var i = 0; i < numChars; i++) {
                try (final var stack = MemoryStack.stackPush()) {
                    final var c = chars[i];
                    final var shape = fontShapes.createGlyphShape(c);
                    // Index 0 is always the exceptions since that's the space character
                    if (i > 0 && (fontShapes.isGlyphEmpty(c) || MSDFUtils.isShapeEmpty(shape))) {
                        MSDFGen.msdf_shape_free(shape); // Free shape right away
                        shapes.put(c, MemoryUtil.NULL);
                        continue;
                    }
                    final var boundsBuffer = MSDFGenBounds.malloc(1, stack);
                    MSDFUtils.throwIfError(MSDFGen.msdf_shape_get_bounds(shape, boundsBuffer));
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
            }

            final var totalSpriteBorder = spriteBorder << 1;
            final var actualSpriteSize = spriteSize - totalSpriteBorder;
            final var scale = (double) actualSpriteSize / Math.max(maxWidth, maxHeight);

            // Render glyphs to atlas image
            var index = 0;
            final var shapeIterator = Char2LongMaps.fastIterable(shapes);
            for (final var entry : shapeIterator) {
                try (final var stack = MemoryStack.stackPush()) {
                    final var shape = entry.getLongValue();
                    final var atlasX = (index % sizeInSlots) * spriteSize;
                    final var atlasY = (index / sizeInSlots) * spriteSize;
                    if (shape == MemoryUtil.NULL) {
                        // Blit the missing glyph texture for all unsupported/empty slots
                        atlasGraphics.drawImage(missingGlyphImage, atlasX, atlasY, spriteSize, spriteSize, null);
                        index++;
                        continue;
                    }
                    MSDFUtils.scaleShape(shape, scale); // Scale to default size of font
                    MSDFUtils.throwIfError(MSDFGen.msdf_shape_edge_colors_simple(shape, 3.0));
                    final var boundsBuffer = MSDFGenBounds.malloc(1, stack);
                    MSDFUtils.throwIfError(MSDFGen.msdf_shape_get_bounds(shape, boundsBuffer));
                    final var bbWidth = boundsBuffer.r() - boundsBuffer.l();
                    final var bbHeight = boundsBuffer.t() - boundsBuffer.b();
                    final var tx = -boundsBuffer.l();
                    final var ty = -boundsBuffer.b() + (actualSpriteSize - bbHeight);
                    final var sdfTx = tx + ((double) (actualSpriteSize >> 1) - (bbWidth * 0.5)) + spriteBorder;
                    final var sdfTy = ty - ((double) (actualSpriteSize >> 1) - (bbHeight * 0.5)) + spriteBorder;
                    // @formatter:off
                    MSDFUtils.renderShapeToImage(renderType, spriteSize, spriteSize, shape,
                        1.0, 1.0, sdfTx, sdfTy, sdfRange,
                        atlasImage, atlasX, atlasY);
                    // @formatter:on
                    final var c = entry.getCharKey();
                    final var metrics = Objects.requireNonNull(fontShapes.getGlyphMetrics(c, scale));
                    final var width = metrics.getWidth();
                    final var height = metrics.getHeight();
                    final var bearingX = metrics.getBearingX();
                    final var bearingY = metrics.getBearingY();
                    final var u = (1F / atlasWidth) * atlasX;
                    final var v = (1F / atlasHeight) * atlasY;
                    synchronized (this) {
                        if (maxGlyphWidth < width) {
                            maxGlyphWidth = width;
                        }
                        if (maxGlyphHeight < height) {
                            maxGlyphHeight = height;
                        }
                        if (maxGlyphBearingX < bearingX) {
                            maxGlyphBearingX = bearingX;
                        }
                        if (maxGlyphBearingY < bearingY) {
                            maxGlyphBearingY = bearingY;
                        }
                        glyphSprites.put(c, new DefaultGlyphSprite(metrics, spriteSize, spriteSize, u, v));
                    }
                    MSDFGen.msdf_shape_free(shape);
                    index++;
                }
            }
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error("Could not load glyph data for font {}: {}",
                fontLocation,
                Exceptions.toFancyString(error));
        }

        // Fill remaining slots with missing sprites for consistency
        final var numShapes = shapes.size();
        final var numEmptySlots = (sizeInSlots * sizeInSlots) - numShapes;
        for (var i = 0; i < numEmptySlots; i++) {
            final var contIndex = numShapes + i;
            atlasGraphics.drawImage(missingGlyphImage,
                (contIndex % sizeInSlots) * spriteSize,
                (contIndex / sizeInSlots) * spriteSize,
                null);
        }

        atlasGraphics.dispose();
        dump(atlasImage, fontLocation);
        RenderSystem.recordRenderCall(() -> uploadTexture(atlasImage));
    }

    void rebuild() {
        isReady.set(false);
        rebuildBlocking();
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
    public int getSpriteBorder() {
        return spriteBorder;
    }

    @Override
    public int getMaxGlyphWidth() {
        return maxGlyphWidth;
    }

    @Override
    public int getMaxGlyphHeight() {
        return maxGlyphHeight;
    }

    @Override
    public int getMaxGlyphBearingX() {
        return maxGlyphBearingX;
    }

    @Override
    public int getMaxGlyphBearingY() {
        return maxGlyphBearingY;
    }

    @Override
    public float getUScale() {
        return uScale;
    }

    @Override
    public float getVScale() {
        return vScale;
    }

    @Override
    public CharSet getSupportedChars() {
        return font.getSupportedChars().toSet();
    }

    @Override
    public void dispose() {
        GL11.glDeleteTextures(textureId);
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

    @Override
    public double getSDFRange() {
        return sdfRange;
    }
}
