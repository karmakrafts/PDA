/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics.font;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.karma.pda.api.client.render.graphics.FontAtlas;
import io.karma.pda.api.client.render.graphics.FontRenderer;
import io.karma.pda.api.common.app.theme.font.Font;
import io.karma.pda.api.common.app.theme.font.FontVariant;
import io.karma.pda.api.common.color.ColorProvider;
import io.karma.pda.api.common.util.Constants;
import io.karma.pda.api.common.util.Exceptions;
import io.karma.pda.api.common.util.RectangleCorner;
import io.karma.pda.common.PDAMod;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.util.msdfgen.MSDFGen;

import java.util.HashMap;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * @author Alexander Hinze
 * @since 04/05/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultFontRenderer implements FontRenderer, ResourceManagerReloadListener {
    public static final DefaultFontRenderer INSTANCE = new DefaultFontRenderer();

    // @formatter:off
    private static final Function<FontAtlasContext, RenderType> RENDER_TYPE = Util.memoize(ctx -> {
        final var fontAtlas = ctx.atlas;
        final var fontLocation = fontAtlas.getFont().getLocation();
        return RenderType.create(String.format("pda_display_font__%s_%s", fontLocation.getNamespace(), fontLocation.getPath()),
            DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.TRIANGLES, 256, false, false,
            RenderType.CompositeState.builder()
                .setCullState(RenderStateShard.NO_CULL)
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> INSTANCE.getShader(ctx)))
                .setOutputState(ctx.outputState)
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setTexturingState(RenderStateShard.DEFAULT_TEXTURING)
                .setLayeringState(RenderStateShard.POLYGON_OFFSET_LAYERING)
                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                .setTextureState(new RenderStateShard.EmptyTextureStateShard(
                    () -> RenderSystem.setShaderTexture(0, fontAtlas.getTextureId()),
                    () -> {}
                ))
                .createCompositeState(false));
    });
    // @formatter:on
    private final HashMap<FontAtlasKey, DefaultFontAtlas> fontAtlasCache = new HashMap<>();
    private ShaderInstance shader;

    private float renderGlyph(final float x, final float y, final int zIndex, final char c, final FontVariant font,
                              final Matrix4f matrix, final VertexConsumer buffer, final ColorProvider colorProvider) {
        final var atlas = getFontAtlas(font);
        if (!atlas.isReady()) {
            return 0; // Don't render anything until the atlas is rebuilt
        }

        // Grab sprite properties
        final var sprite = atlas.getGlyphSprite(c);
        final var spriteSize = sprite.getSize();

        // Retrieve glyph metrics
        final var metrics = sprite.getMetrics();
        final var width = metrics.getWidth();
        final var height = metrics.getHeight();

        // Find scale factor and re-scale metrics
        final var scale = font.getSize() / atlas.getMaxGlyphHeight();
        final var scaledWidth = scale * width;
        final var scaledHeight = scale * height;
        final var scaledAscent = scale * metrics.getAscent();
        final var scaledDescent = scale * metrics.getDescent();
        final var scaledBearingX = scale * metrics.getBearingX();
        final var scaledBearingY = scale * metrics.getBearingY();
        final var yOffset = scaledAscent - scaledBearingY + scaledDescent;

        // Compute vertex positions for glyph quad
        final var minX = x + scaledBearingX;
        final var minY = y + yOffset;
        final var maxX = minX + scaledWidth;
        final var maxY = minY + scaledHeight;
        final var z = (float) zIndex; // Do cast once instead of per-vertex

        // Compute glyph UVs
        final var uUnit = atlas.getUScale();
        final var vUnit = atlas.getVScale();
        final var minU = Math.fma(uUnit, ((float) spriteSize - width) * 0.5F, sprite.getU());
        final var minV = Math.fma(vUnit, ((float) spriteSize - height) * 0.5F, sprite.getV());
        final var maxU = Math.fma(uUnit, width, minU);
        final var maxV = Math.fma(vUnit, height, minV);

        // Retrieve colors for shared vertices
        final var colorTR = colorProvider.getColor(RectangleCorner.TOP_RIGHT);
        final var colorBL = colorProvider.getColor(RectangleCorner.BOTTOM_LEFT);

        // @formatter:off
        // First triangle
        buffer.vertex(matrix, minX, minY, z).uv(minU, minV)
            .color(colorProvider.getColor(RectangleCorner.TOP_LEFT)).endVertex();
        buffer.vertex(matrix, maxX, minY, z).uv(maxU, minV)
            .color(colorTR).endVertex();
        buffer.vertex(matrix, minX, maxY, z).uv(minU, maxV)
            .color(colorBL).endVertex();
        // Second triangle
        buffer.vertex(matrix, maxX, minY, z).uv(maxU, minV)
            .color(colorTR).endVertex();
        buffer.vertex(matrix, maxX, maxY, z).uv(maxU, maxV)
            .color(colorProvider.getColor(RectangleCorner.BOTTOM_RIGHT)).endVertex();
        buffer.vertex(matrix, minX, maxY, z).uv(minU, maxV)
            .color(colorBL).endVertex();
        // @formatter:on

        // Make sure we return the scaled number of pixels to advance on the X-axis while rendering multiple glyphs
        return scale * metrics.getAdvanceX();
    }

    private RenderType getRenderType(final Font font, final RenderStateShard.OutputStateShard outputState) {
        final var variant = font.asVariant();
        return RENDER_TYPE.apply(new FontAtlasContext(getFontAtlas(variant), variant.getSize(), outputState));
    }

    @Override
    public FontAtlas getFontAtlas(final Font font) {
        return fontAtlasCache.computeIfAbsent(new FontAtlasKey(font.getLocation(), font.getVariationAxes()),
            location -> {
                final var family = font.getFamily();
                return new DefaultFontAtlas(font.asVariant(),
                    family.getGlyphSpriteSize(),
                    family.getGlyphSpriteBorder(),
                    family.getDistanceFieldRange(),
                    MSDFGen.MSDF_BITMAP_TYPE_MSDF);
            });
    }

    @Override
    public int getLineHeight(final Font font) {
        final var fontVariant = font.asVariant();
        final var atlas = getFontAtlas(fontVariant);
        final var scale = fontVariant.getSize() / atlas.getMaxGlyphHeight();
        return (int) (scale * atlas.getLineHeight());
    }

    @Override
    public int getStringWidth(final Font font, final CharSequence s) {
        final var fontVariant = font.asVariant();
        final var atlas = getFontAtlas(fontVariant);
        final var scale = fontVariant.getSize() / atlas.getMaxGlyphHeight();
        var width = 0F;
        for (var i = 0; i < s.length(); i++) {
            final var metrics = atlas.getGlyphSprite(s.charAt(i)).getMetrics();
            width += scale * metrics.getAdvanceX();
        }
        return (int) width;
    }

    //@Override
    //public int render(final int x, final int y, final int zIndex, final char c, final ColorProvider colorProvider,
    //                  final Font font, final GraphicsContext context) {
    //    final var buffer = context.getBufferSource().getBuffer(getRenderType(font));
    //    final var matrix = context.getTransform();
    //    return (int) renderGlyph(x, y, zIndex, c, font.asVariant(), matrix, buffer, colorProvider);
    //}
    //
    //@Override
    //public int render(final int x, final int y, final int zIndex, final CharSequence s,
    //                  final ColorProvider colorProvider, final Font font, final GraphicsContext context) {
    //    final var buffer = context.getBufferSource().getBuffer(getRenderType(font));
    //    final var matrix = context.getTransform();
    //    final var fontVariant = font.asVariant();
    //    var offset = 0F;
    //    for (var i = 0; i < s.length(); i++) {
    //        offset += renderGlyph(x + offset, y, zIndex, s.charAt(i), fontVariant, matrix, buffer, colorProvider);
    //    }
    //    return (int) offset;
    //}
    //
    //@Override
    //public int render(final int x, final int y, final int zIndex, final CharSequence s,
    //                  final IntFunction<ColorProvider> colorFunction, final Font font, final GraphicsContext context) {
    //    final var buffer = context.getBufferSource().getBuffer(getRenderType(font));
    //    final var matrix = context.getTransform();
    //    final var fontVariant = font.asVariant();
    //    var offset = 0F;
    //    for (var i = 0; i < s.length(); i++) {
    //        offset += renderGlyph(x + offset,
    //            y,
    //            zIndex,
    //            s.charAt(i),
    //            fontVariant,
    //            matrix,
    //            buffer,
    //            colorFunction.apply(i));
    //    }
    //    return (int) offset;
    //}

    @Override
    public int render(int x, int y, char c, Font font, ColorProvider color) {
        return 0;
    }

    @Override
    public int render(int x, int y, CharSequence text, Font font, ColorProvider color) {
        return 0;
    }

    @Override
    public int render(int x, int y, int maxWidth, CharSequence text, Font font, ColorProvider color) {
        return 0;
    }

    @Override
    public int render(int x, int y, int maxWidth, int maxHeight, CharSequence text, Font font, ColorProvider color) {
        return 0;
    }

    @Override
    public int render(int x, int y, int maxWidth, CharSequence text, CharSequence cutoffSuffix, Font font,
                      ColorProvider color) {
        return 0;
    }

    @Override
    public int render(int x, int y, int maxWidth, int maxHeight, CharSequence text, CharSequence cutoffSuffix,
                      Font font, ColorProvider color) {
        return 0;
    }

    // Functions with per-glyph colors

    @Override
    public int render(int x, int y, CharSequence text, Font font, IntFunction<ColorProvider> color) {
        return 0;
    }

    @Override
    public int render(int x, int y, int maxWidth, CharSequence text, Font font, IntFunction<ColorProvider> color) {
        return 0;
    }

    @Override
    public int render(int x, int y, int maxWidth, int maxHeight, CharSequence text, Font font,
                      IntFunction<ColorProvider> color) {
        return 0;
    }

    @Override
    public int render(int x, int y, int maxWidth, CharSequence text, CharSequence cutoffSuffix, Font font,
                      IntFunction<ColorProvider> color) {
        return 0;
    }

    @Override
    public int render(int x, int y, int maxWidth, int maxHeight, CharSequence text, CharSequence cutoffSuffix,
                      Font font, IntFunction<ColorProvider> color) {
        return 0;
    }

    @Override
    public void onResourceManagerReload(final @NotNull ResourceManager manager) {
        PDAMod.LOGGER.debug("Rebuilding font atlas cache");
        for (final var fontAtlas : fontAtlasCache.values()) {
            Minecraft.getInstance().execute(fontAtlas::rebuild);
        }
    }

    private ShaderInstance getShader(final FontAtlasContext ctx) {
        final var fontAtlas = ctx.atlas;
        final var range = fontAtlas.getSDFRange() * ctx.atlas.getFont().getFamily().getDistanceFieldRange();
        shader.safeGetUniform("PxRange").set((ctx.scale / fontAtlas.getSpriteSize()) * range);
        return shader;
    }

    @ApiStatus.Internal
    public void setupEarly() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegisterShaders);
    }

    private void onRegisterShaders(final RegisterShadersEvent event) {
        try {
            PDAMod.LOGGER.debug("Loading font renderer shaders");
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                new ResourceLocation(Constants.MODID, "display_font"),
                DefaultVertexFormat.POSITION_TEX_COLOR), shader -> this.shader = shader);
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error("Could not load font renderer shader: {}", Exceptions.toFancyString(error));
        }
    }

    @ApiStatus.Internal
    public void setup() {
        ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(this);
    }

    /*

    @Override
    public void text(final int x, final int y, final int maxWidth, final CharSequence text) {
        final var state = getState();
        final var fontRenderer = context.getFontRenderer();
        final var font = state.getFont();
        final var fontVariant = font instanceof FontVariant variant ? variant : font.getDefaultVariant();
        final var fontAtlas = fontRenderer.getFontAtlas(fontVariant);

        final var scale = fontVariant.getSize() / fontAtlas.getMaxGlyphHeight();
        final var maxGlyphWidth = (int) (fontAtlas.getMaxGlyphWidth() * scale);
        final var actualMaxWidth = maxWidth - (maxGlyphWidth >> 1); // Use half the largest char as tolerance

        final var buffer = new StringBuilder();
        final var charCount = text.length();
        final var lastIndex = charCount - 1;
        final var lineHeight = fontRenderer.getLineHeight(fontVariant);
        final var z = state.getZIndex();
        final var brush = state.getBrush();
        var line = 0;

        for (var i = 0; i < charCount; i++) {
            final var lineY = y + (lineHeight * line);
            final var c = text.charAt(i);
            // Add the current character to the buffer until we flush
            buffer.append(c);
            // Handle newlines
            if (c == '\n' && !buffer.isEmpty()) {
                fontRenderer.render(x, lineY, z, buffer, brush, fontVariant, context);
                buffer.delete(0, buffer.length());
                line++;
                continue;
            }
            // Handle horizontal overflow using word boundary algorithm
            if (fontRenderer.getStringWidth(fontVariant, buffer) >= actualMaxWidth && !buffer.isEmpty()) {
                fontRenderer.render(x, lineY, z, buffer, brush, fontVariant, context);
                buffer.delete(0, buffer.length());
                line++;
                continue;
            }
            // Handle remainder if we are at the end and the buffer is not empty
            if (i == lastIndex) {
                fontRenderer.render(x, lineY, z, buffer, brush, fontVariant, context);
            }
        }
    }

     */

    private record FontAtlasKey(ResourceLocation location, Object2FloatMap<String> variationAxes) {
    }

    private record FontAtlasContext(FontAtlas atlas, float scale, RenderStateShard.OutputStateShard outputState) {
    }
}
