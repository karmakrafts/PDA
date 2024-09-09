/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.foundation.client.render;

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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.function.Function;

/**
 * @author Alexander Hinze
 * @since 06/06/2024
 */
@Internal
@OnlyIn(Dist.CLIENT)
public final class ComponentRenderTypes {
    // @formatter:off
    private static ShaderProgram spinnerShader;

    public static final Function<DisplayMode, RenderType> SPINNER = Util.memoize(mode -> Peregrine.createRenderType(it -> it
        .name(String.format("%s:spinner", Constants.MODID))
        .vertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR)
        .mode(Mode.TRIANGLES)
        .culling(false)
        .blendMode(Transparency.TRANSPARENCY)
        .layering(Layering.POLYGON_OFFSET)
        .target(mode.getFramebuffer())
        .shader(spinnerShader)
    ));
    // @formatter:on

    private ComponentRenderTypes() {
    }

    @Internal
    public static void createShaders() {
        // @formatter:off
        spinnerShader = ShaderProgram.create(builder -> builder
            .stage(object -> object
                .type(ShaderType.VERTEX)
                .location(Constants.MODID, "shaders/spinner.vert.glsl")
            )
            .stage(object -> object
                .type(ShaderType.FRAGMENT)
                .location(Constants.MODID, "shaders/spinner.frag.glsl")
            )
            .globalUniforms()
        );
        // @formatter:on
    }
}
