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
import io.karma.pda.api.client.render.shader.uniform.UniformBuffer;
import io.karma.pda.api.client.render.shader.uniform.UniformType;
import io.karma.pda.mod.client.render.texture.StaticTexture;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

/**
 * @author Alexander Hinze
 * @since 13/06/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultShaderProgramBuilder implements ShaderProgramBuilder {
    private static final Consumer<ShaderProgram> IDENTITY_CALLBACK = program -> {
    };

    private final ArrayList<DefaultShaderObject> objects = new ArrayList<>();
    private final LinkedHashMap<String, Uniform> uniforms = new LinkedHashMap<>();
    private final HashMap<String, UniformBuffer> uniformBuffers = new HashMap<>();
    private final LinkedHashMap<String, Object> constants = new LinkedHashMap<>(); // Don't care about (un)boxing here
    private final Object2IntOpenHashMap<String> samplers = new Object2IntOpenHashMap<>();
    private final Int2ObjectArrayMap<IntSupplier> staticSamplers = new Int2ObjectArrayMap<>();
    private final LinkedHashMap<String, Object> defines = new LinkedHashMap<>();
    private VertexFormat format = DefaultVertexFormat.POSITION;
    private Consumer<ShaderProgram> bindCallback = IDENTITY_CALLBACK;
    private Consumer<ShaderProgram> unbindCallback = IDENTITY_CALLBACK;
    private int currentSamplerId;

    // @formatter:off
    DefaultShaderProgramBuilder() {}
    // @formatter:on

    DefaultShaderProgram build() {
        return new DefaultShaderProgram(format,
            objects,
            uniforms,
            uniformBuffers,
            bindCallback,
            unbindCallback,
            samplers,
            constants,
            defines,
            staticSamplers);
    }

    @Override
    public ShaderProgramBuilder define(final String name) {
        if (defines.containsKey(name)) {
            throw new IllegalArgumentException(String.format("Define '%s' already exists", name));
        }
        defines.put(name, 1);
        return this;
    }

    @Override
    public ShaderProgramBuilder define(final String name, final boolean value) {
        if (defines.containsKey(name)) {
            throw new IllegalArgumentException(String.format("Define '%s' already exists", name));
        }
        defines.put(name, value ? 1 : 0);
        return this;
    }

    @Override
    public ShaderProgramBuilder define(final String name, final int value) {
        if (defines.containsKey(name)) {
            throw new IllegalArgumentException(String.format("Define '%s' already exists", name));
        }
        defines.put(name, value);
        return this;
    }

    @Override
    public ShaderProgramBuilder define(final String name, final float value) {
        if (defines.containsKey(name)) {
            throw new IllegalArgumentException(String.format("Define '%s' already exists", name));
        }
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
    public ShaderProgramBuilder sampler(final String name) {
        if (samplers.containsKey(name)) {
            throw new IllegalArgumentException(String.format("Sampler '%s' is already defined", name));
        }
        samplers.put(name, currentSamplerId++);
        return this;
    }

    @Override
    public ShaderProgramBuilder sampler(final String name, final IntSupplier textureId) {
        if (samplers.containsKey(name)) {
            throw new IllegalArgumentException(String.format("Sampler '%s' is already defined", name));
        }
        final var id = currentSamplerId++;
        samplers.put(name, id);
        staticSamplers.put(id, textureId);
        return this;
    }

    @Override
    public ShaderProgramBuilder sampler(final String name, final ResourceLocation location) {
        return sampler(name, StaticTexture.get(location)::getId);
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
        if (uniforms.containsKey(name)) {
            throw new IllegalArgumentException(String.format("Uniform '%s' is already defined", name));
        }
        if (!type.isSupported()) {
            throw new IllegalArgumentException("Unsupported uniform type");
        }
        uniforms.put(name, type.create(name));
        return this;
    }

    @Override
    public ShaderProgramBuilder uniforms(final String name, final UniformBuffer buffer) {
        if (uniformBuffers.containsKey(name)) {
            throw new IllegalArgumentException(String.format("Uniform block '%s' is already defined", name));
        }
        uniformBuffers.put(name, buffer);
        return this;
    }

    @Override
    public ShaderProgramBuilder onBind(final Consumer<ShaderProgram> bindCallback) {
        if (this.bindCallback == IDENTITY_CALLBACK) {
            this.bindCallback = bindCallback;
            return this;
        }
        this.bindCallback = this.bindCallback.andThen(bindCallback);
        return this;
    }

    @Override
    public ShaderProgramBuilder onUnbind(final Consumer<ShaderProgram> unbindCallback) {
        if (this.unbindCallback == IDENTITY_CALLBACK) {
            this.unbindCallback = unbindCallback;
            return this;
        }
        this.unbindCallback = this.unbindCallback.andThen(unbindCallback);
        return this;
    }
}
