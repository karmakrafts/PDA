/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.graphics;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.karma.pda.mod.PDAMod;
import io.karma.pda.api.client.render.display.DisplayMode;
import io.karma.pda.api.util.Constants;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class GraphicsRenderTypes {
    public static final GraphicsRenderTypes INSTANCE = new GraphicsRenderTypes();
    // @formatter:off
    public static final Function<DisplayMode, RenderType> COLOR_TRIS = Util.memoize(displayMode ->
        RenderType.create(String.format("pda_display_color_tris__%s", displayMode),
            DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES, 256, false, false,
            RenderType.CompositeState.builder()
                .setCullState(RenderStateShard.NO_CULL)
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> INSTANCE.getColorShader(displayMode)))
                .setOutputState(displayMode.getOutputState())
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setLayeringState(RenderStateShard.POLYGON_OFFSET_LAYERING)
                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                .createCompositeState(false)));
    private static final Function<ModeAndTextureKey, RenderType> COLOR_TEXTURE_TRIS = Util.memoize(key -> {
        final var texture = key.texture;
        final var displayMode = key.mode;
        return RenderType.create(String.format("pda_display_color_tex_tris__%s__%s_%s", displayMode, texture.getNamespace(), texture.getPath()),
            DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.TRIANGLES, 256, false, false,
            RenderType.CompositeState.builder()
                .setCullState(RenderStateShard.NO_CULL)
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> INSTANCE.getColorTextureShader(displayMode)))
                .setOutputState(displayMode.getOutputState())
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setTexturingState(RenderStateShard.DEFAULT_TEXTURING)
                .setLayeringState(RenderStateShard.POLYGON_OFFSET_LAYERING)
                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                .setTextureState(new RenderStateShard.EmptyTextureStateShard(
                    () -> RenderSystem.setShaderTexture(0, texture),
                    () -> {}
                ))
                .createCompositeState(false));
    });
    private ShaderInstance colorShader;
    private ShaderInstance colorTextureShader;

    private GraphicsRenderTypes() {}
    // @formatter:on

    public static RenderType getColorTextureTris(final DisplayMode displayMode, final ResourceLocation texture) {
        return COLOR_TEXTURE_TRIS.apply(new ModeAndTextureKey(displayMode, texture));
    }

    @ApiStatus.Internal
    public void setupEarly() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegisterShaders);
    }

    private void onRegisterShaders(final RegisterShadersEvent event) {
        try {
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                new ResourceLocation(Constants.MODID, "display_color"),
                DefaultVertexFormat.POSITION_COLOR), shader -> colorShader = shader);
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                new ResourceLocation(Constants.MODID, "display_color_tex"),
                DefaultVertexFormat.POSITION_TEX_COLOR), shader -> colorTextureShader = shader);
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error("Could not register default GFX render type shaders: {}", error.getMessage());
        }
    }

    public ShaderInstance getColorShader(final DisplayMode displayMode) {
        return colorShader;
    }

    public ShaderInstance getColorTextureShader(final DisplayMode displayMode) {
        return colorTextureShader;
    }

    private record ModeAndTextureKey(DisplayMode mode, ResourceLocation texture) {
    }
}
