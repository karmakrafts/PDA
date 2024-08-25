/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader.uniform;

import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.util.HashUtils;
import io.karma.pda.api.util.MathUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 * @author Alexander Hinze
 * @since 13/06/2024
 */
@OnlyIn(Dist.CLIENT)
public final class Vector2fUniform implements GenericUniform<Vector2f> {
    private final String name;
    private final Vector2f value = new Vector2f();
    private boolean hasChanged = true;

    Vector2fUniform(final String name, final Object defaultValue) {
        this.name = name;
        if (!(defaultValue instanceof Vector2fc)) {
            throw new IllegalArgumentException("Default value is not a Vector2fc");
        }
        value.set((Vector2fc) defaultValue);
    }

    @Override
    public void set(final Vector2f value) {
        if (this.value.equals(value, MathUtils.EPSILON)) {
            return;
        }
        this.value.set(value);
        hasChanged = true;
    }

    @Override
    public Vector2f get() {
        return value;
    }

    @Override
    public void notifyUpdate() {
        hasChanged = true;
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
    public boolean requiresUpdate() {
        return hasChanged;
    }

    @Override
    public void apply(final ShaderProgram program) {
        if (!hasChanged) {
            return;
        }
        GL20.glUniform2f(program.getUniformLocation(name), value.x, value.y);
        hasChanged = false;
    }

    @Override
    public void upload(final UniformBuffer buffer, final long address) {
        if (!hasChanged) {
            return;
        }
        try (final var stack = MemoryStack.stackPush()) {
            final var offset = buffer.getFieldOffset(name);
            MemoryUtil.memCopy(MemoryUtil.memAddress(stack.floats(value.x, value.y)),
                address + offset,
                getType().getSize());
        }
        hasChanged = false;
    }

    @Override
    public int hashCode() {
        return HashUtils.combine(name.hashCode(), getType().getHash());
    }
}
