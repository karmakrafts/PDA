/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader.uniform;

import io.karma.pda.api.client.render.shader.ShaderProgram;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.ARBGPUShaderInt64;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 * @author Alexander Hinze
 * @since 24/08/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultLongUniform implements Uniform {
    private final String name;
    private long value;
    private boolean hasChanged = true;

    DefaultLongUniform(final String name, final Object defaultValue) {
        this.name = name;
        if (!(defaultValue instanceof Number number)) {
            throw new IllegalArgumentException("Default value is not a number");
        }
        value = number.longValue();
    }

    public void setLong(final long value) {
        if (this.value == value) {
            return;
        }
        this.value = value;
        hasChanged = true;
    }

    @Override
    public UniformType getType() {
        return DefaultUniformType.LONG;
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
        ARBGPUShaderInt64.glUniform1i64ARB(program.getUniformLocation(name), value);
        hasChanged = false;
    }

    @Override
    public void upload(final UniformBuffer buffer, final long address) {
        if (!hasChanged) {
            return;
        }
        try (final var stack = MemoryStack.stackPush()) {
            final var offset = buffer.getFieldOffset(name);
            MemoryUtil.memCopy(MemoryUtil.memAddress(stack.longs(value)), address + offset, getType().getSize());
        }
        hasChanged = false;
    }

    @Override
    public void notifyUpdate() {
        hasChanged = true;
    }
}
