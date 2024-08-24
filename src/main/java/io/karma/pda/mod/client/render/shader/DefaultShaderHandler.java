/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import io.karma.pda.api.client.ClientAPI;
import io.karma.pda.api.client.render.shader.ShaderHandler;
import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.client.render.shader.ShaderProgramBuilder;
import io.karma.pda.api.client.render.shader.uniform.DefaultUniformType;
import io.karma.pda.api.client.render.shader.uniform.UniformBuffer;
import io.karma.pda.api.client.render.shader.uniform.UniformBufferBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 13/06/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultShaderHandler implements ShaderHandler {
    public static final DefaultShaderHandler INSTANCE = new DefaultShaderHandler();
    private UniformBuffer globalUniforms;

    // @formatter:off
    private DefaultShaderHandler() {}
    // @formatter:on

    @Internal
    public void setup() { // @formatter:off
        globalUniforms = createUniformBuffer(builder -> builder
            .uniform("ProjMat", DefaultUniformType.FLOAT_MAT4.derive(new Matrix4f().identity()))
            .uniform("ModelViewMat", DefaultUniformType.FLOAT_MAT4.derive(new Matrix4f().identity()))
            .uniform("ColorModulator", DefaultUniformType.FLOAT_VEC4.derive(new Vector4f(1F)))
            .uniform("Time", DefaultUniformType.FLOAT)
            .onBind((prog, buff) -> {
                final var cache = buff.getCache();
                cache.getMatrix4f("ProjMat").set(RenderSystem.getProjectionMatrix());
                cache.getMatrix4f("ModelViewMat").set(RenderSystem.getModelViewMatrix());
                cache.getVector4f("ColorModulator").set(new Vector4f(RenderSystem.getShaderColor()));
                cache.getFloat("Time").setFloat(ClientAPI.getShaderTime());
            })
        ); // @formatter:on
    }

    @Override
    public ShaderProgram create(final Consumer<ShaderProgramBuilder> callback) {
        final var builder = new DefaultShaderProgramBuilder();
        callback.accept(builder);
        return builder.build();
    }

    @Override
    public UniformBuffer createUniformBuffer(final Consumer<UniformBufferBuilder> callback) {
        final var builder = new DefaultUniformBufferBuilder();
        callback.accept(builder);
        return builder.build();
    }

    @Override
    public UniformBuffer getGlobalUniforms() {
        return globalUniforms;
    }
}
