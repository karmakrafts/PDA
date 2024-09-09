/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.graphics;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import io.karma.pda.api.client.render.display.DisplayMode;
import io.karma.pda.api.util.Constants;
import io.karma.peregrine.api.Peregrine;
import io.karma.peregrine.api.shader.ShaderProgram;
import io.karma.peregrine.api.shader.ShaderType;
import io.karma.peregrine.api.state.Layering;
import io.karma.peregrine.api.state.Transparency;
import net.minecraft.Util;
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

    public static final Function<DisplayMode, RenderType> COLOR_TRIS = Util.memoize(mode -> Peregrine.createRenderType(it -> it
        .name(String.format("%s:color_tris_%s", Constants.MODID, mode.getSpec()))
        .vertexFormat(DefaultVertexFormat.POSITION_COLOR)
        .mode(Mode.TRIANGLES)
        .culling(false)
        .blendMode(Transparency.TRANSPARENCY)
        .layering(Layering.POLYGON_OFFSET)
        .target(mode.getFramebuffer())
        .shader(colorShader)
    ));

    private static ShaderProgram colorTexShader;

    private static final Function<ModeAndTextureKey, RenderType> COLOR_TEXTURE_TRIS = Util.memoize(key -> Peregrine.createRenderType(it -> it
        .name(String.format("%s:color_tex_tris_%s", Constants.MODID, key.mode.getSpec()))
        .vertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR)
        .mode(Mode.TRIANGLES)
        .culling(false)
        .blendMode(Transparency.TRANSPARENCY)
        .layering(Layering.POLYGON_OFFSET)
        .target(key.mode.getFramebuffer())
        .shader(colorTexShader)
    ));

    private GraphicsRenderTypes() {}
    // @formatter:on

    @Internal
    public static void createShaders() { // @formatter:off
        colorShader = ShaderProgram.create(builder -> builder
            .stage(object -> object
                .type(ShaderType.VERTEX)
                .location(Constants.MODID, "shaders/display_color.vert.glsl")
            )
            .stage(object -> object
                .type(ShaderType.FRAGMENT)
                .location(Constants.MODID, "shaders/display_color.frag.glsl")
            )
            .globalUniforms()
        );
        colorTexShader = ShaderProgram.create(builder -> builder
            .stage(object -> object
                .type(ShaderType.VERTEX)
                .location(Constants.MODID, "shaders/display_color_tex.vert.glsl")
            )
            .stage(object -> object
                .type(ShaderType.FRAGMENT)
                .location(Constants.MODID, "shaders/display_color_tex.frag.glsl")
            )
            .sampler("Sampler0")
            .globalUniforms()
        );
    } // @formatter:on

    public static RenderType getColorTextureTris(final DisplayMode displayMode, final ResourceLocation texture) {
        return COLOR_TEXTURE_TRIS.apply(new ModeAndTextureKey(displayMode, texture));
    }

    private record ModeAndTextureKey(DisplayMode mode, ResourceLocation texture) {
    }
}
