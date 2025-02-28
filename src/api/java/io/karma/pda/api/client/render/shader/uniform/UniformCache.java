/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader.uniform;

import io.karma.pda.api.client.render.shader.ShaderProgram;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

/**
 * @author Alexander Hinze
 * @since 13/06/2024
 */
@OnlyIn(Dist.CLIENT)
public interface UniformCache {
    void applyAll(final ShaderProgram program);

    void uploadAll(final UniformBuffer block);

    void updateAll();

    Map<String, Uniform> getAll();

    Uniform get(final String name);

    default DefaultFloatUniform getFloat(final String name) {
        final var uniform = get(name);
        if (uniform.getType() != DefaultUniformType.FLOAT) {
            throw new IllegalStateException(String.format("Uniform %s is not a float uniform", name));
        }
        return (DefaultFloatUniform) uniform;
    }

    default Vector2fUniform getVector2f(final String name) {
        final var uniform = get(name);
        if (uniform.getType() != DefaultUniformType.FLOAT_VEC2) {
            throw new IllegalStateException(String.format("Uniform %s is not a float vector2 uniform", name));
        }
        return (Vector2fUniform) uniform;
    }

    default Vector3fUniform getVector3f(final String name) {
        final var uniform = get(name);
        if (uniform.getType() != DefaultUniformType.FLOAT_VEC3) {
            throw new IllegalStateException(String.format("Uniform %s is not a float vector3 uniform", name));
        }
        return (Vector3fUniform) uniform;
    }

    default Vector4fUniform getVector4f(final String name) {
        final var uniform = get(name);
        if (uniform.getType() != DefaultUniformType.FLOAT_VEC4) {
            throw new IllegalStateException(String.format("Uniform %s is not a float vector4 uniform", name));
        }
        return (Vector4fUniform) uniform;
    }

    default Matrix4fUniform getMatrix4f(final String name) {
        final var uniform = get(name);
        if (uniform.getType() != DefaultUniformType.FLOAT_MAT4) {
            throw new IllegalStateException(String.format("Uniform %s is not a float matrix4 uniform", name));
        }
        return (Matrix4fUniform) uniform;
    }

    default DefaultIntUniform getInt(final String name) {
        final var uniform = get(name);
        if (uniform.getType() != DefaultUniformType.INT) {
            throw new IllegalStateException(String.format("Uniform %s is not an integer uniform", name));
        }
        return (DefaultIntUniform) uniform;
    }
}
