/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader.uniform;

import io.karma.pda.api.client.render.shader.ShaderProgram;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL20;

/**
 * @author Alexander Hinze
 * @since 11/08/2024
 */
@OnlyIn(Dist.CLIENT)
public class DefaultIntUniform implements IntUniform {
    private final String name;
    private int value;
    private boolean hasChanged = true;

    DefaultIntUniform(final String name, final Object defaultValue) {
        this.name = name;
        if (!(defaultValue instanceof Integer)) {
            throw new IllegalArgumentException("Default value is not an integer");
        }
        value = (Integer) defaultValue;
    }

    @Override
    public int getInt() {
        return value;
    }

    @Override
    public void setInt(final int value) {
        if (this.value == value) {
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
        return DefaultUniformType.INT;
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
        GL20.glUniform1i(program.getUniformCache().getLocation(name), value);
        hasChanged = false;
    }
}
