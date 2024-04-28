/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.karma.pda.api.common.util.Constants;
import io.karma.pda.client.ClientEventHandler;
import io.karma.pda.client.render.display.DisplayRenderer;
import io.karma.pda.common.PDAMod;
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
    private ShaderInstance colorShader;
    private ShaderInstance colorTextureShader;
    private ShaderInstance spinnerShader;

    // @formatter:off
    public static final RenderType COLOR_TRIS = RenderType.create("pda_display_color_tris",
        DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES, 256, false, false,
        RenderType.CompositeState.builder()
            .setCullState(RenderStateShard.NO_CULL)
            .setShaderState(new RenderStateShard.ShaderStateShard(INSTANCE::getColorShader))
            .setOutputState(DisplayRenderer.DISPLAY_OUTPUT)
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setLayeringState(RenderStateShard.POLYGON_OFFSET_LAYERING)
            .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
            .createCompositeState(false));

    public static final Function<ResourceLocation, RenderType> COLOR_TEXTURE_TRIS = Util.memoize(texture ->
        RenderType.create(String.format("pda_display_color_tex_tris__%s_%s", texture.getNamespace(), texture.getPath()),
            DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.TRIANGLES, 256, false, false,
            RenderType.CompositeState.builder()
                .setCullState(RenderStateShard.NO_CULL)
                .setShaderState(new RenderStateShard.ShaderStateShard(INSTANCE::getColorTextureShader))
                .setOutputState(DisplayRenderer.DISPLAY_OUTPUT)
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setTexturingState(RenderStateShard.DEFAULT_TEXTURING)
                .setLayeringState(RenderStateShard.POLYGON_OFFSET_LAYERING)
                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                .setTextureState(new RenderStateShard.EmptyTextureStateShard(
                    () -> RenderSystem.setShaderTexture(0, texture),
                    () -> {}
                ))
                .createCompositeState(false)));

    public static final RenderType SPINNER = RenderType.create("pda_display_spinner",
        DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.TRIANGLES, 256, false, false,
        RenderType.CompositeState.builder()
            .setCullState(RenderStateShard.NO_CULL)
            .setShaderState(new RenderStateShard.ShaderStateShard(INSTANCE::getSpinnerShader))
            .setOutputState(DisplayRenderer.DISPLAY_OUTPUT)
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setLayeringState(RenderStateShard.POLYGON_OFFSET_LAYERING)
            .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
            .createCompositeState(false));

    private GraphicsRenderTypes() {}
    // @formatter:on

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
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                new ResourceLocation(Constants.MODID, "display_spinner"),
                DefaultVertexFormat.POSITION_TEX_COLOR), shader -> spinnerShader = shader);
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error("Could not register default GFX render type shaders: {}", error.getMessage());
        }
    }

    public ShaderInstance getColorShader() {
        return colorShader;
    }

    public ShaderInstance getColorTextureShader() {
        return colorTextureShader;
    }

    public ShaderInstance getSpinnerShader() {
        spinnerShader.safeGetUniform("Time").set(ClientEventHandler.INSTANCE.getShaderTime());
        return spinnerShader;
    }
}
