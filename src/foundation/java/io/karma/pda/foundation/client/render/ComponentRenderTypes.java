/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.foundation.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.karma.pda.api.client.ClientAPI;
import io.karma.pda.api.client.render.display.DisplayMode;
import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.client.render.shader.ShaderType;
import io.karma.pda.api.util.Constants;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
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
    private static final ShaderProgram SPINNER_SHADER = ClientAPI.getShaderFactory().create(builder -> builder
        .shader(object -> object
            .type(ShaderType.VERTEX)
            .location(Constants.MODID, "shaders/spinner.vert.glsl")
            .defaultPreProcessor()
        )
        .shader(object -> object
            .type(ShaderType.FRAGMENT)
            .location(Constants.MODID, "shaders/spinner.frag.glsl")
            .defaultPreProcessor()
        )
        .defaultUniforms()
        .uniformTime()
    );
    public static final Function<DisplayMode, RenderType> SPINNER = Util.memoize(displayMode ->
        RenderType.create(String.format("pda_display_spinner__%s", displayMode),
            DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.TRIANGLES, 256, false, false,
            RenderType.CompositeState.builder()
                .setCullState(RenderStateShard.NO_CULL)
                .setShaderState(SPINNER_SHADER.asStateShard())
                .setOutputState(displayMode.getOutputState())
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setLayeringState(RenderStateShard.POLYGON_OFFSET_LAYERING)
                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                .createCompositeState(false)));
    // @formatter:on

    private ComponentRenderTypes() {
    }

    @Internal
    public static void createShaders() {
    }
}
