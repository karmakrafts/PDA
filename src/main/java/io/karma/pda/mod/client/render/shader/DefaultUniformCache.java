/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.shader;

import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.client.render.shader.uniform.Uniform;
import io.karma.pda.api.client.render.shader.uniform.UniformBuffer;
import io.karma.pda.api.client.render.shader.uniform.UniformCache;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL33;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 13/06/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultUniformCache implements UniformCache {
    private final LinkedHashMap<String, Uniform> uniforms;

    DefaultUniformCache(final LinkedHashMap<String, Uniform> uniforms) {
        this.uniforms = uniforms;
    }

    @Internal
    public void merge(final UniformCache cache) {
        uniforms.putAll(cache.getAll());
    }

    @Override
    public Map<String, Uniform> getAll() {
        return uniforms;
    }

    @Override
    public void applyAll(final ShaderProgram program) {
        for (final var uniform : uniforms.values()) {
            uniform.apply(program);
        }
    }

    @Override
    public void uploadAll(final UniformBuffer buffer) {
        final var uniforms = this.uniforms.values();
        // Find out if mapping the memory is worth it and return if we don't have any updates
        var needsRemap = false;
        for (final var uniform : uniforms) {
            if (!uniform.requiresUpdate()) {
                continue;
            }
            needsRemap = true;
            break;
        }
        if (!needsRemap) {
            return;
        }
        // Map UBO into memory so we can memcpy into it directly
        GL15.glBindBuffer(GL33.GL_UNIFORM_BUFFER, buffer.getId());
        final var address = GL15.nglMapBuffer(GL33.GL_UNIFORM_BUFFER, GL15.GL_WRITE_ONLY);
        for (final var uniform : uniforms) {
            uniform.upload(buffer, address);
        }
        GL15.glUnmapBuffer(GL33.GL_UNIFORM_BUFFER);
        GL15.glBindBuffer(GL33.GL_UNIFORM_BUFFER, 0);
    }

    @Override
    public void updateAll() {
        for (final var uniform : uniforms.values()) {
            uniform.notifyUpdate();
        }
    }

    @Override
    public Uniform get(final String name) {
        return Objects.requireNonNull(uniforms.get(name));
    }
}
