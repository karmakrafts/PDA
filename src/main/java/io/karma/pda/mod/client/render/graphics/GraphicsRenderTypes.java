/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.graphics;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.karma.pda.api.client.render.display.DisplayMode;
import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.client.render.shader.ShaderType;
import io.karma.pda.api.util.Constants;
import io.karma.pda.mod.client.render.shader.DefaultShaderHandler;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.function.Function;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class GraphicsRenderTypes {
    // @formatter:off
    private static ShaderProgram colorShader;
    public static final Function<DisplayMode, RenderType> COLOR_TRIS = Util.memoize(displayMode ->
        RenderType.create(String.format("pda_display_color_tris__%s", displayMode.getSpec().name()),
            DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES, 256, false, false,
            RenderType.CompositeState.builder()
                .setCullState(RenderStateShard.NO_CULL)
                .setShaderState(colorShader.asStateShard())
                .setOutputState(displayMode.getOutputState())
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setLayeringState(RenderStateShard.POLYGON_OFFSET_LAYERING)
                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                .createCompositeState(false)));
    private static ShaderProgram colorTexShader;
    private static final Function<ModeAndTextureKey, RenderType> COLOR_TEXTURE_TRIS = Util.memoize(key -> {
        final var texture = key.texture;
        final var displayMode = key.mode;
        return RenderType.create(String.format("pda_display_color_tex_tris__%s__%s_%s", displayMode.getSpec().name(), texture.getNamespace(), texture.getPath()),
            DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.TRIANGLES, 256, false, false,
            RenderType.CompositeState.builder()
                .setCullState(RenderStateShard.NO_CULL)
                .setShaderState(colorTexShader.asStateShard())
                .setOutputState(displayMode.getOutputState())
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setTexturingState(RenderStateShard.DEFAULT_TEXTURING)
                .setLayeringState(RenderStateShard.POLYGON_OFFSET_LAYERING)
                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                .setTextureState(new RenderStateShard.EmptyTextureStateShard(
                    () -> colorTexShader.setSampler("Sampler0", texture),
                    () -> {}
                ))
                .createCompositeState(false));
    });

    private GraphicsRenderTypes() {}
    // @formatter:on

    @Internal
    public static void createShaders() { // @formatter:off
        colorShader = DefaultShaderHandler.INSTANCE.create(builder -> builder
            .shader(object -> object
                .type(ShaderType.VERTEX)
                .location(Constants.MODID, "shaders/display_color.vert.glsl")
                .defaultPreProcessor()
            )
            .shader(object -> object
                .type(ShaderType.FRAGMENT)
                .location(Constants.MODID, "shaders/display_color.frag.glsl")
                .defaultPreProcessor()
            )
            .defaultUniforms()
        );
        colorTexShader = DefaultShaderHandler.INSTANCE.create(builder -> builder
            .shader(object -> object
                .type(ShaderType.VERTEX)
                .location(Constants.MODID, "shaders/display_color_tex.vert.glsl")
                .defaultPreProcessor()
            )
            .shader(object -> object
                .type(ShaderType.FRAGMENT)
                .location(Constants.MODID, "shaders/display_color_tex.frag.glsl")
                .defaultPreProcessor()
            )
            .sampler("Sampler0")
            .defaultUniforms()
        );
    } // @formatter:on

    public static RenderType getColorTextureTris(final DisplayMode displayMode, final ResourceLocation texture) {
        return COLOR_TEXTURE_TRIS.apply(new ModeAndTextureKey(displayMode, texture));
    }

    private record ModeAndTextureKey(DisplayMode mode, ResourceLocation texture) {
    }
}
