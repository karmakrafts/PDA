/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader.uniform;

import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.util.HashUtils;
import io.karma.pda.api.util.MathUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 13/06/2024
 */
@OnlyIn(Dist.CLIENT)
public final class Vector4fUniform implements GenericUniform<Vector4f> {
    private final String name;
    private final Vector4f value = new Vector4f();
    private boolean hasChanged = true;

    Vector4fUniform(final String name, final Object defaultValue) {
        this.name = name;
        if (!(defaultValue instanceof Vector4fc)) {
            throw new IllegalArgumentException("Default value is not a Vector4fc");
        }
        value.set((Vector4fc) defaultValue);
    }

    @Override
    public void set(final Vector4f value) {
        if (this.value.equals(value, MathUtils.EPSILON)) {
            return;
        }
        this.value.set(value);
        hasChanged = true;
    }

    @Override
    public Vector4f get() {
        return value;
    }

    @Override
    public void notifyUpdate() {
        hasChanged = true;
    }

    @Override
    public boolean requiresUpdate() {
        return hasChanged;
    }

    @Override
    public DefaultUniformType getType() {
        return DefaultUniformType.FLOAT_VEC4;
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
        GL20.glUniform4f(program.getUniformLocation(name), value.x, value.y, value.z, value.w);
        hasChanged = false;
    }

    @Override
    public void upload(final UniformBuffer buffer, final long address) {
        if (!hasChanged) {
            return;
        }
        try (final var stack = MemoryStack.stackPush()) {
            final var offset = buffer.getFieldOffset(name);
            MemoryUtil.memCopy(MemoryUtil.memAddress(stack.floats(value.x, value.y, value.z, value.w)),
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
