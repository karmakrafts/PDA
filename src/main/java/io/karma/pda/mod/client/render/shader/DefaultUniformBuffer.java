/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.shader;

import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.client.render.shader.uniform.Uniform;
import io.karma.pda.api.client.render.shader.uniform.UniformBuffer;
import io.karma.pda.api.client.render.shader.uniform.UniformCache;
import io.karma.pda.api.dispose.Disposable;
import io.karma.pda.api.util.HashUtils;
import io.karma.pda.api.util.LogMarkers;
import io.karma.pda.mod.PDAMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.*;

import java.util.LinkedHashMap;
import java.util.function.BiConsumer;

/**
 * @author Alexander Hinze
 * @since 23/08/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultUniformBuffer implements UniformBuffer, Disposable {
    private final DefaultUniformCache cache;
    private final BiConsumer<ShaderProgram, UniformBuffer> bindCallback;
    private final BiConsumer<ShaderProgram, UniformBuffer> unbindCallback;
    private final int size;
    private final int bindingPoint;
    private int id = -1;

    public DefaultUniformBuffer(final LinkedHashMap<String, Uniform> uniforms,
                                final BiConsumer<ShaderProgram, UniformBuffer> bindCallback,
                                final BiConsumer<ShaderProgram, UniformBuffer> unbindCallback,
                                final int bindingPoint) {
        this.bindCallback = bindCallback;
        this.unbindCallback = unbindCallback;
        this.bindingPoint = bindingPoint;
        cache = new DefaultUniformCache(uniforms);
        size = cache.getAll().values().stream().mapToInt(u -> u.getType().getAlignedSize()).sum();
        PDAMod.DISPOSITION_HANDLER.register(this);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getBindingPoint() {
        return bindingPoint;
    }

    @Override
    public void setup(final String name, final ShaderProgram program) {
        if (id == -1) {
            id = GL15.glGenBuffers();
            GL30.glBindBuffer(GL33.GL_UNIFORM_BUFFER, id);
            GL15.glBufferData(GL33.GL_UNIFORM_BUFFER, size, GL20.GL_STATIC_DRAW);
            GL30.glBindBuffer(GL33.GL_UNIFORM_BUFFER, 0);
            PDAMod.LOGGER.debug(LogMarkers.RENDERER, "Created new uniform buffer object {} with {} bytes", id, size);
        }
        final var blockIndex = program.getUniformBlockIndex(name);
        GL31.glUniformBlockBinding(program.getId(), blockIndex, bindingPoint);
        PDAMod.LOGGER.debug(LogMarkers.RENDERER,
            "Associated uniform block index {} with binding point {}",
            blockIndex,
            bindingPoint);

        GL30.glBindBufferBase(GL33.GL_UNIFORM_BUFFER, bindingPoint, id);
    }

    @Override
    public void bind(final String name, final ShaderProgram program) {
        bindCallback.accept(program, this);
        cache.uploadAll(this);
    }

    @Override
    public void unbind(final String name, final ShaderProgram program) {
        unbindCallback.accept(program, this);
    }

    @Override
    public UniformCache getCache() {
        return cache;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int getFieldOffset(final String name) {
        var offset = 0;
        for (final var uniform : cache.getAll().values()) {
            if (uniform.getName().equals(name)) {
                break;
            }
            offset += uniform.getType().getAlignedSize();
        }
        return offset;
    }

    @Override
    public void dispose() {
        GL15.glDeleteBuffers(id);
    }

    @Override
    public int hashCode() {
        return HashUtils.combine(bindingPoint, cache.hashCode());
    }
}
