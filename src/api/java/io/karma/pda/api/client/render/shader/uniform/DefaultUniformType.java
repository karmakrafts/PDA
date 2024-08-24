/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader.uniform;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL;

import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;

/**
 * @author Alexander Hinze
 * @since 13/06/2024
 */
@OnlyIn(Dist.CLIENT)
public enum DefaultUniformType implements UniformType {
    // @formatter:off
    FLOAT       (DefaultFloatUniform::new,  Float.BYTES,    1,  Float.BYTES,      0F,             () -> true),
    FLOAT_VEC2  (Vector2fUniform::new,      Float.BYTES,    2,  Float.BYTES * 2,  new Vector2f(), () -> true),
    FLOAT_VEC3  (Vector3fUniform::new,      Float.BYTES,    3,  Float.BYTES * 4,  new Vector3f(), () -> true),
    FLOAT_VEC4  (Vector4fUniform::new,      Float.BYTES,    4,  Float.BYTES * 4,  new Vector4f(), () -> true),
    FLOAT_MAT4  (Matrix4fUniform::new,      Float.BYTES,    16, Float.BYTES * 16, new Matrix4f(), () -> true),
    INT         (DefaultIntUniform::new,    Integer.BYTES,  1,  Integer.BYTES,    0,              () -> true),
    LONG        (DefaultLongUniform::new,   Long.BYTES,     1,  Long.BYTES,       0L,             () -> GL.getCapabilities().GL_ARB_gpu_shader_int64);
    // @formatter:on

    private final BiFunction<String, Object, Uniform> factory;
    private final int componentSize;
    private final int componentCount;
    private final int alignment;
    private final Object defaultValue;
    private final BooleanSupplier isSupported;

    DefaultUniformType(final BiFunction<String, Object, Uniform> factory,
                       final int componentSize,
                       final int componentCount,
                       final int alignment,
                       final Object defaultValue,
                       final BooleanSupplier isSupported) {
        this.factory = factory;
        this.componentSize = componentSize;
        this.componentCount = componentCount;
        this.alignment = alignment;
        this.defaultValue = defaultValue;
        this.isSupported = isSupported;
    }

    @Override
    public Uniform create(final String name, final Object defaultValue) {
        return factory.apply(name, defaultValue);
    }

    @Override
    public int getComponentSize() {
        return componentSize;
    }

    @Override
    public int getComponentCount() {
        return componentCount;
    }

    @Override
    public int getAlignment() {
        return alignment;
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean isSupported() {
        return isSupported.getAsBoolean();
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    @Override
    public int getHash() {
        return toString().hashCode();
    }
}
