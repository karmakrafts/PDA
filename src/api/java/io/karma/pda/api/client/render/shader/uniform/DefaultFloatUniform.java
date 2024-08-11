/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader.uniform;

import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.util.MathUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL20;

/**
 * @author Alexander Hinze
 * @since 13/06/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultFloatUniform implements FloatUniform {
    private final String name;
    private float value;
    private boolean hasChanged = true;

    DefaultFloatUniform(final String name, final Object defaultValue) {
        this.name = name;
        if (!(defaultValue instanceof Float)) {
            throw new IllegalArgumentException("Default value is not a float");
        }
        value = (Float) defaultValue;
    }

    @Override
    public float getFloat() {
        return value;
    }

    @Override
    public void setFloat(final float value) {
        if (MathUtils.equals(this.value, value, MathUtils.EPSILON)) {
            return;
        }
        this.value = value;
        hasChanged = true;
    }

    @Override
    public void notifyUpdate() {
        hasChanged = true;
    }

    @Override
    public UniformType getType() {
        return DefaultUniformType.FLOAT;
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
        GL20.glUniform1f(program.getUniformCache().getLocation(name), value);
        hasChanged = false;
    }
}
