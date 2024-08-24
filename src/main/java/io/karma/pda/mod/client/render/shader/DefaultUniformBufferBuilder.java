/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.shader;

import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.client.render.shader.uniform.Uniform;
import io.karma.pda.api.client.render.shader.uniform.UniformBuffer;
import io.karma.pda.api.client.render.shader.uniform.UniformBufferBuilder;
import io.karma.pda.api.client.render.shader.uniform.UniformType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.LinkedHashMap;
import java.util.function.BiConsumer;

/**
 * @author Alexander Hinze
 * @since 23/08/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultUniformBufferBuilder implements UniformBufferBuilder {
    private static final BiConsumer<ShaderProgram, UniformBuffer> IDENTITY_CALLBACK = (s, b) -> {
    };
    private static int nextBindingPoint;

    private final LinkedHashMap<String, Uniform> uniforms = new LinkedHashMap<>();
    private BiConsumer<ShaderProgram, UniformBuffer> bindCallback = IDENTITY_CALLBACK;
    private BiConsumer<ShaderProgram, UniformBuffer> unbindCallback = IDENTITY_CALLBACK;

    @Override
    public UniformBufferBuilder uniform(final String name, final UniformType type) {
        if (uniforms.containsKey(name)) {
            throw new IllegalArgumentException(String.format("Uniform '%s' is already defined", name));
        }
        uniforms.put(name, type.create(name));
        return this;
    }

    @Override
    public UniformBufferBuilder onBind(final BiConsumer<ShaderProgram, UniformBuffer> callback) {
        if (bindCallback == IDENTITY_CALLBACK) {
            bindCallback = callback;
            return this;
        }
        bindCallback = bindCallback.andThen(callback);
        return this;
    }

    @Override
    public UniformBufferBuilder onUnbind(final BiConsumer<ShaderProgram, UniformBuffer> callback) {
        if (unbindCallback == IDENTITY_CALLBACK) {
            unbindCallback = callback;
            return this;
        }
        unbindCallback = unbindCallback.andThen(callback);
        return this;
    }

    public DefaultUniformBuffer build() {
        return new DefaultUniformBuffer(uniforms, bindCallback, unbindCallback, nextBindingPoint++);
    }
}
