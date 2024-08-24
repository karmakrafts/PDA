/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader.uniform;

import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.util.HashUtils;
import io.karma.pda.api.util.MathUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.util.Objects;

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
        if (!(defaultValue instanceof Number number)) {
            throw new IllegalArgumentException("Default value is not a float");
        }
        value = number.floatValue();
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
        GL20.glUniform1f(program.getUniformLocation(name), value);
        hasChanged = false;
    }

    @Override
    public boolean requiresUpdate() {
        return hasChanged;
    }

    @Override
    public void upload(final UniformBuffer buffer, final long address) {
        if (!hasChanged) {
            return;
        }
        try (final var stack = MemoryStack.stackPush()) {
            final var offset = buffer.getFieldOffset(name);
            MemoryUtil.memCopy(MemoryUtil.memAddress(stack.floats(value)), address + offset, getType().getSize());
        }
        hasChanged = false;
    }

    @Override
    public int hashCode() {
        return HashUtils.combine(name.hashCode(), getType().getHash());
    }
}
