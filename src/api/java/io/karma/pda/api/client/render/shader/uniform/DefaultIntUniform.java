/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader.uniform;

import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.util.HashUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 11/08/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultIntUniform implements IntUniform {
    private final String name;
    private int value;
    private boolean hasChanged = true;

    DefaultIntUniform(final String name, final Object defaultValue) {
        this.name = name;
        if (!(defaultValue instanceof Number number)) {
            throw new IllegalArgumentException("Default value is not an integer");
        }
        value = number.intValue();
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
    public boolean requiresUpdate() {
        return hasChanged;
    }

    @Override
    public void apply(final ShaderProgram program) {
        if (!hasChanged) {
            return;
        }
        GL20.glUniform1i(program.getUniformLocation(name), value);
        hasChanged = false;
    }

    @Override
    public void upload(final UniformBuffer buffer, final long address) {
        if (!hasChanged) {
            return;
        }
        try (final var stack = MemoryStack.stackPush()) {
            final var offset = buffer.getFieldOffset(name);
            MemoryUtil.memCopy(MemoryUtil.memAddress(stack.ints(value)), address + offset, getType().getSize());
        }
        hasChanged = false;
    }

    @Override
    public int hashCode() {
        return HashUtils.combine(name.hashCode(), getType().getHash());
    }
}
