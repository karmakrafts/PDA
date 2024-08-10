/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.karma.pda.api.client.render.shader.ShaderObjectBuilder;
import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.client.render.shader.ShaderProgramBuilder;
import io.karma.pda.api.client.render.shader.uniform.DefaultUniformType;
import io.karma.pda.api.client.render.shader.uniform.Uniform;
import io.karma.pda.api.client.render.shader.uniform.UniformType;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 13/06/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultShaderProgramBuilder implements ShaderProgramBuilder {
    private final ArrayList<DefaultShaderObject> objects = new ArrayList<>();
    private final HashMap<String, Uniform> uniforms = new HashMap<>();
    private final HashMap<String, Object> constants = new HashMap<>(); // Don't care about (un)boxing here
    private final Object2IntOpenHashMap<String> samplers = new Object2IntOpenHashMap<>();
    private final Object2IntOpenHashMap<String> defines = new Object2IntOpenHashMap<>();
    private VertexFormat format = DefaultVertexFormat.POSITION;
    private Consumer<ShaderProgram> bindCallback;
    private Consumer<ShaderProgram> unbindCallback;

    // @formatter:off
    DefaultShaderProgramBuilder() {}
    // @formatter:on

    DefaultShaderProgram build() {
        return new DefaultShaderProgram(format,
            objects,
            uniforms,
            bindCallback,
            unbindCallback,
            samplers,
            constants,
            defines);
    }

    @Override
    public ShaderProgramBuilder define(final String name) {
        defines.put(name, 1);
        return this;
    }

    @Override
    public ShaderProgramBuilder define(final String name, final boolean value) {
        defines.put(name, value ? 1 : 0);
        return this;
    }

    @Override
    public ShaderProgramBuilder define(final String name, final int value) {
        defines.put(name, value);
        return this;
    }

    @Override
    public ShaderProgramBuilder constant(final String name, final int value) {
        if (constants.containsKey(name)) {
            throw new IllegalArgumentException(String.format("Constant '%s' is already defined", name));
        }
        constants.put(name, value);
        return this;
    }

    @Override
    public ShaderProgramBuilder constant(final String name, final float value) {
        if (constants.containsKey(name)) {
            throw new IllegalArgumentException(String.format("Constant '%s' is already defined", name));
        }
        constants.put(name, value);
        return this;
    }

    @Override
    public ShaderProgramBuilder constant(final String name, final boolean value) {
        if (constants.containsKey(name)) {
            throw new IllegalArgumentException(String.format("Constant '%s' is already defined", name));
        }
        constants.put(name, value);
        return this;
    }

    @Override
    public ShaderProgramBuilder sampler(final String name, final int id) {
        if (samplers.containsKey(name)) {
            throw new IllegalArgumentException(String.format("Sampler '%s' is already defined", name));
        }
        samplers.put(name, id);
        return this;
    }

    @Override
    public ShaderProgramBuilder defaultUniforms() {
        uniform("ModelViewMat", DefaultUniformType.FLOAT_MAT4.derive(new Matrix4f().identity()));
        uniform("ProjMat", DefaultUniformType.FLOAT_MAT4.derive(new Matrix4f().identity()));
        uniform("ColorModulator", DefaultUniformType.FLOAT_VEC4.derive(new Vector4f(1F)));
        onBind(program -> {
            final var uniformCache = program.getUniformCache();
            uniformCache.getMatrix4f("ModelViewMat").set(RenderSystem.getModelViewMatrix());
            uniformCache.getMatrix4f("ProjMat").set(RenderSystem.getProjectionMatrix());
            uniformCache.getVector4f("ColorModulator").set(new Vector4f(RenderSystem.getShaderColor()));
        });
        return this;
    }

    @Override
    public ShaderProgramBuilder format(final VertexFormat format) {
        this.format = format;
        return this;
    }

    @Override
    public ShaderProgramBuilder shader(final Consumer<ShaderObjectBuilder> callback) {
        final var builder = new DefaultShaderObjectBuilder();
        callback.accept(builder);
        objects.add(builder.build());
        return this;
    }

    @Override
    public ShaderProgramBuilder uniform(final String name, final UniformType type) {
        uniforms.put(name, type.create(name));
        return this;
    }

    @Override
    public ShaderProgramBuilder onBind(final Consumer<ShaderProgram> bindCallback) {
        if (this.bindCallback == null) {
            this.bindCallback = bindCallback;
            return this;
        }
        this.bindCallback = this.bindCallback.andThen(bindCallback);
        return this;
    }

    @Override
    public ShaderProgramBuilder onUnbind(final Consumer<ShaderProgram> unbindCallback) {
        if (this.unbindCallback == null) {
            this.unbindCallback = unbindCallback;
            return this;
        }
        this.unbindCallback = this.unbindCallback.andThen(unbindCallback);
        return this;
    }
}
