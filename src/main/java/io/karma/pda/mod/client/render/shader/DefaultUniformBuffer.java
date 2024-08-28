/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.shader;

import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.client.render.shader.uniform.Uniform;
import io.karma.pda.api.client.render.shader.uniform.UniformBuffer;
import io.karma.pda.api.client.render.shader.uniform.UniformCache;
import io.karma.pda.api.dispose.Disposable;
import io.karma.pda.api.util.LogMarkers;
import io.karma.pda.mod.PDAMod;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
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
    private final Object2IntOpenHashMap<String> fieldOffsets = new Object2IntOpenHashMap<>();
    private final int id;
    private int bufferId = -1;

    public DefaultUniformBuffer(final LinkedHashMap<String, Uniform> uniforms,
                                final BiConsumer<ShaderProgram, UniformBuffer> bindCallback,
                                final BiConsumer<ShaderProgram, UniformBuffer> unbindCallback,
                                final int id) {
        this.bindCallback = bindCallback;
        this.unbindCallback = unbindCallback;
        this.id = id;
        cache = new DefaultUniformCache(uniforms);
        size = cache.getAll().values().stream().mapToInt(u -> u.getType().getAlignedSize()).sum();

        // Compute all field offsets ahead of time
        var offset = 0;
        for (final var uniform : uniforms.entrySet()) {
            fieldOffsets.put(uniform.getKey(), offset);
            offset += uniform.getValue().getType().getAlignedSize();
        }

        PDAMod.DISPOSITION_HANDLER.register(this);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getBufferId() {
        return bufferId;
    }

    @Override
    public void setup(final String name, final ShaderProgram program) {
        if (bufferId == -1) {
            bufferId = GL15.glGenBuffers();
            GL15.glBindBuffer(GL33.GL_UNIFORM_BUFFER, bufferId);
            GL15.glBufferData(GL33.GL_UNIFORM_BUFFER, size, GL20.GL_STATIC_DRAW);
            GL15.glBindBuffer(GL33.GL_UNIFORM_BUFFER, 0);
            PDAMod.LOGGER.debug(LogMarkers.RENDERER,
                "Created new uniform buffer object {} with {} bytes",
                bufferId,
                size);
        }
        final var blockIndex = program.getUniformBlockIndex(name);
        GL31.glUniformBlockBinding(program.getId(), blockIndex, id);
        PDAMod.LOGGER.debug(LogMarkers.RENDERER,
            "Associated uniform block index {} with binding point {}",
            blockIndex,
            id);
        GL30.glBindBufferBase(GL33.GL_UNIFORM_BUFFER, id, bufferId);
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
        return fieldOffsets.getOrDefault(name, 0);
    }

    @Override
    public void dispose() {
        GL15.glDeleteBuffers(bufferId);
    }

    @Override
    public int hashCode() {
        return cache.hashCode();
    }

    @Override
    public String toString() {
        return String.format("DefaultUniformBuffer[id=%d,size=%d]", bufferId, size);
    }
}
