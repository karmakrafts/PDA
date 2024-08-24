/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader.uniform;

import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.util.MathUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 * @author Alexander Hinze
 * @since 13/06/2024
 */
@OnlyIn(Dist.CLIENT)
public final class Matrix4fUniform implements GenericUniform<Matrix4f> {
    private final String name;
    private final Matrix4f value = new Matrix4f();
    private boolean hasChanged = true;

    Matrix4fUniform(final String name, final Object defaultValue) {
        this.name = name;
        if (!(defaultValue instanceof Matrix4fc)) {
            throw new IllegalArgumentException("Default value is not a Matrix4fc");
        }
        value.set((Matrix4fc) defaultValue);
    }

    @Override
    public void set(final Matrix4f value) {
        if (this.value.equals(value, MathUtils.EPSILON)) {
            return;
        }
        this.value.set(value);
        hasChanged = true;
    }

    @Override
    public Matrix4f get() {
        return value;
    }

    @Override
    public void notifyUpdate() {
        hasChanged = true;
    }

    @Override
    public DefaultUniformType getType() {
        return DefaultUniformType.FLOAT_MAT4;
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
        try (final var stack = MemoryStack.stackPush()) {
            GL20.glUniformMatrix4fv(program.getUniformLocation(name), false, value.get(stack.mallocFloat(16)));
        }
        hasChanged = false;
    }

    @Override
    public void upload(final UniformBuffer buffer, final long address) {
        if (!hasChanged) {
            return;
        }
        try (final var stack = MemoryStack.stackPush()) {
            final var offset = buffer.getFieldOffset(name);
            MemoryUtil.memCopy(MemoryUtil.memAddress(value.get(stack.mallocFloat(16))),
                address + offset,
                getType().getSize());
        }
        hasChanged = false;
    }
}
