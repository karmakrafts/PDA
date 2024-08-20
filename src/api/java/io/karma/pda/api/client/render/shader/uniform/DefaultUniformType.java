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

import java.util.function.BiFunction;

/**
 * @author Alexander Hinze
 * @since 13/06/2024
 */
@OnlyIn(Dist.CLIENT)
public enum DefaultUniformType implements UniformType {
    // @formatter:off
    FLOAT       (DefaultFloatUniform::new,  Float.BYTES,    1,  0F),
    FLOAT_VEC2  (Vector2fUniform::new,      Float.BYTES,    2,  new Vector2f()),
    FLOAT_VEC3  (Vector3fUniform::new,      Float.BYTES,    3,  new Vector3f()),
    FLOAT_VEC4  (Vector4fUniform::new,      Float.BYTES,    4,  new Vector4f()),
    FLOAT_MAT4  (Matrix4fUniform::new,      Float.BYTES,    12, new Matrix4f()),
    INT         (DefaultIntUniform::new,    Integer.BYTES,  1,  0);
    // @formatter:on

    private final BiFunction<String, Object, Uniform> factory;
    private final int componentSize;
    private final int componentCount;
    private final Object defaultValue;

    DefaultUniformType(final BiFunction<String, Object, Uniform> factory,
                       final int componentSize,
                       final int componentCount,
                       final Object defaultValue) {
        this.factory = factory;
        this.componentSize = componentSize;
        this.componentCount = componentCount;
        this.defaultValue = defaultValue;
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
    public Object getDefaultValue() {
        return defaultValue;
    }
}
