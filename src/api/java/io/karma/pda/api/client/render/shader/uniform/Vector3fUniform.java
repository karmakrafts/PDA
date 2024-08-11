/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader.uniform;

import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.util.MathUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.opengl.GL20;

/**
 * @author Alexander Hinze
 * @since 13/06/2024
 */
@OnlyIn(Dist.CLIENT)
public final class Vector3fUniform implements GenericUniform<Vector3f> {
    private final String name;
    private final Vector3f value = new Vector3f();
    private boolean hasChanged = true;

    Vector3fUniform(final String name, final Object defaultValue) {
        this.name = name;
        if (!(defaultValue instanceof Vector3fc)) {
            throw new IllegalArgumentException("Default value is not a Vector3fc");
        }
        value.set((Vector3fc) defaultValue);
    }

    @Override
    public void set(final Vector3f value) {
        if (this.value.equals(value, MathUtils.EPSILON)) {
            return;
        }
        this.value.set(value);
        hasChanged = true;
    }

    @Override
    public Vector3f get() {
        return value;
    }

    @Override
    public void notifyUpdate() {
        hasChanged = true;
    }

    @Override
    public DefaultUniformType getType() {
        return DefaultUniformType.FLOAT_VEC3;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void apply(final ShaderProgram program) {
        if (!hasChanged) {
            return;
        }
        GL20.glUniform3f(program.getUniformCache().getLocation(name), value.x, value.y, value.z);
        hasChanged = false;
    }
}
