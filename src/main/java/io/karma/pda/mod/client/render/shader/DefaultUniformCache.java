/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.shader;

import io.karma.pda.api.client.render.shader.uniform.Uniform;
import io.karma.pda.api.client.render.shader.uniform.UniformCache;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL20;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 13/06/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultUniformCache implements UniformCache {
    private final Object2IntOpenHashMap<String> locations = new Object2IntOpenHashMap<>();
    private final HashMap<String, Uniform> uniforms;
    private final DefaultShaderProgram program;

    DefaultUniformCache(final DefaultShaderProgram program, final HashMap<String, Uniform> uniforms) {
        this.program = program;
        this.uniforms = uniforms;
    }

    @Override
    public void clear() {
        locations.clear();
    }

    @Override
    public Uniform get(final String name) {
        return Objects.requireNonNull(uniforms.get(name));
    }

    @Override
    public int getLocation(final String name) {
        return locations.computeIfAbsent(name, (final String s) -> GL20.glGetUniformLocation(program.getId(), s));
    }
}
