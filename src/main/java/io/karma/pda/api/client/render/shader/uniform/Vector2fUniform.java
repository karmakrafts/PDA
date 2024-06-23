/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader.uniform;

import io.karma.pda.api.client.render.shader.ShaderProgram;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.lwjgl.opengl.GL20;

/**
 * @author Alexander Hinze
 * @since 13/06/2024
 */
@OnlyIn(Dist.CLIENT)
public final class Vector2fUniform implements GenericUniform<Vector2f> {
    private final String name;
    private final Vector2f value = new Vector2f();
    private boolean hasChanged;

    Vector2fUniform(final String name, final Object defaultValue) {
        this.name = name;
        if (!(defaultValue instanceof Vector2fc)) {
            throw new IllegalArgumentException("Default value is not a Vector2fc");
        }
        value.set((Vector2fc) defaultValue);
    }

    @Override
    public void set(final Vector2f value) {
        this.value.set(value);
        hasChanged = true;
    }

    @Override
    public Vector2f get() {
        return value;
    }

    @Override
    public DefaultUniformType getType() {
        return DefaultUniformType.FLOAT_VEC2;
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
        GL20.glUniform2f(program.getUniformCache().getLocation(name), value.x, value.y);
        hasChanged = false;
    }
}
