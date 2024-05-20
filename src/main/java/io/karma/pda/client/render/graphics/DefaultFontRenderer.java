/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.karma.pda.api.client.render.graphics.FontAtlas;
import io.karma.pda.api.client.render.graphics.FontRenderer;
import io.karma.pda.api.client.render.graphics.GraphicsContext;
import io.karma.pda.api.common.app.theme.font.Font;
import io.karma.pda.api.common.app.theme.font.FontVariant;
import io.karma.pda.api.common.color.ColorProvider;
import io.karma.pda.api.common.util.Constants;
import io.karma.pda.api.common.util.Exceptions;
import io.karma.pda.api.common.util.RectangleCorner;
import io.karma.pda.client.render.display.DisplayRenderer;
import io.karma.pda.common.PDAMod;
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
    private static final Function<FontAtlas, RenderType> RENDER_TYPE = Util.memoize(fontAtlas -> {
        final var fontLocation = fontAtlas.getFont().getLocation();
        return RenderType.create(String.format("pda_display_font__%s_%s", fontLocation.getNamespace(), fontLocation.getPath()),
            DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.TRIANGLES, 256, false, false,
            RenderType.CompositeState.builder()
                .setCullState(RenderStateShard.NO_CULL)
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> INSTANCE.getShader(fontAtlas)))
                .setOutputState(DisplayRenderer.DISPLAY_OUTPUT)
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

    private final HashMap<ResourceLocation, DefaultFontAtlas> fontAtlasCache = new HashMap<>();
    private ShaderInstance shader;

    private int renderGlyph(final int x, final int y, final int zIndex, final char c, final FontVariant font,
                            final Matrix4f matrix, final VertexConsumer buffer, final ColorProvider colorProvider) {
        final var atlas = getFontAtlas(font);
        if (!atlas.isReady()) {
            return 0; // Don't render anything until the atlas is rebuilt
        }

        final var sprite = atlas.getGlyphSprite(c);
        final var spriteWidth = sprite.getWidth();
        final var spriteHeight = sprite.getHeight();
        final var spriteBorder = atlas.getSpriteBorder() << 1;
        final var actualSpriteWidth = spriteWidth - spriteBorder;
        final var actualSpriteHeight = spriteHeight - spriteBorder;
        final var z = (float) zIndex;

        final var metrics = sprite.getMetrics();
        final var fontSize = font.getSize();
        final var width = metrics.getWidth();
        final var height = metrics.getHeight();
        //final var width = (int) (((float) metrics.getWidth() / actualSpriteWidth) * fontSize);
        //final var height = (int) (((float) metrics.getHeight() / actualSpriteHeight) * fontSize);

        final var minX = x + metrics.getBearingX();
        final var minY = (y + (atlas.getMaxGlyphBearingY() - height)) + (height - metrics.getBearingY());
        final var maxX = minX + width;
        final var maxY = minY + height;

        final var uUnit = atlas.getUScale();
        final var vUnit = atlas.getVScale();
        final var xOffset = ((spriteWidth - width) >> 1);
        final var yOffset = ((spriteHeight - height) >> 1);
        final var minU = sprite.getU() + uUnit * xOffset;
        final var minV = sprite.getV() + vUnit * yOffset;
        final var maxU = minU + uUnit * width;
        final var maxV = minV + vUnit * height;

        final var colorTR = colorProvider.getColor(RectangleCorner.TOP_RIGHT);
        final var colorBL = colorProvider.getColor(RectangleCorner.BOTTOM_LEFT);
        final var colorTL = colorProvider.getColor(RectangleCorner.TOP_LEFT);
        final var colorBR = colorProvider.getColor(RectangleCorner.BOTTOM_RIGHT);

        // First triangle
        buffer.vertex(matrix, minX, minY, z).uv(minU, minV).color(colorTL).endVertex();
        buffer.vertex(matrix, maxX, minY, z).uv(maxU, minV).color(colorTR).endVertex();
        buffer.vertex(matrix, minX, maxY, z).uv(minU, maxV).color(colorBL).endVertex();
        // Second triangle
        buffer.vertex(matrix, maxX, minY, z).uv(maxU, minV).color(colorTR).endVertex();
        buffer.vertex(matrix, maxX, maxY, z).uv(maxU, maxV).color(colorBR).endVertex();
        buffer.vertex(matrix, minX, maxY, z).uv(minU, maxV).color(colorBL).endVertex();

        return metrics.getAdvance();
    }

    private RenderType getRenderType(final Font font) {
        return RENDER_TYPE.apply(getFontAtlas(font));
    }

    @Override
    public FontAtlas getFontAtlas(final Font font) {
        return fontAtlasCache.computeIfAbsent(font.getLocation(),
            location -> new DefaultFontAtlas(font, 32, 2, 4.0, MSDFGen.MSDF_BITMAP_TYPE_MSDF));
    }

    @Override
    public int renderGlyph(final int x, final int y, final int zIndex, final char c, final ColorProvider colorProvider,
                           final Font font, final GraphicsContext context) {
        final var buffer = context.getBufferSource().getBuffer(getRenderType(font));
        final var matrix = context.getTransform();
        final var fontVariant = font instanceof FontVariant variant ? variant : font.getDefaultVariant();
        return renderGlyph(x, y, zIndex, c, fontVariant, matrix, buffer, colorProvider);
    }

    @Override
    public void render(final int x, final int y, final int zIndex, final String s, final ColorProvider colorProvider,
                       final Font font, final GraphicsContext context) {
        final var buffer = context.getBufferSource().getBuffer(getRenderType(font));
        final var matrix = context.getTransform();
        final var fontVariant = font instanceof FontVariant variant ? variant : font.getDefaultVariant();
        var offset = 0;
        for (var i = 0; i < s.length(); i++) {
            offset += renderGlyph(x + offset, y, zIndex, s.charAt(i), fontVariant, matrix, buffer, colorProvider);
        }
    }

    @Override
    public void render(final int x, final int y, final int zIndex, final String s,
                       final IntFunction<ColorProvider> colorFunction, final Font font, final GraphicsContext context) {
        final var buffer = context.getBufferSource().getBuffer(getRenderType(font));
        final var matrix = context.getTransform();
        final var fontVariant = font instanceof FontVariant variant ? variant : font.getDefaultVariant();
        var offset = 0;
        for (var i = 0; i < s.length(); i++) {
            final var color = colorFunction.apply(i);
            offset += renderGlyph(x + offset, y, zIndex, s.charAt(i), fontVariant, matrix, buffer, color);
        }
    }

    @Override
    public void onResourceManagerReload(final @NotNull ResourceManager manager) {
        PDAMod.LOGGER.debug("Rebuilding font atlas cache");
        for (final var fontAtlas : fontAtlasCache.values()) {
            fontAtlas.rebuild();
        }
    }

    private ShaderInstance getShader(final FontAtlas atlas) {
        // TODO: update PxRange uniform
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
}
