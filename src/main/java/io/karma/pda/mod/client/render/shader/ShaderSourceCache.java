/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.shader;

import io.karma.pda.api.client.render.shader.ShaderObject;
import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.util.LogMarkers;
import io.karma.pda.mod.PDAMod;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL20;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Alexander Hinze
 * @since 26/08/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ShaderSourceCache extends AbstractShaderCache {
    ShaderSourceCache() {
        PDAMod.LOGGER.debug(LogMarkers.RENDERER, "Creating shader source cache");
    }

    @Override
    public void save(final Path directory,
                     final ResourceManager manager,
                     final ShaderProgram program,
                     final ShaderObject object) {
        final var fingerprint = getFingerprint(program, object);
        final var file = directory.resolve(String.format("%s.glsl", fingerprint));
        saveText(file, GL20.glGetShaderSource(object.getId()));
    }

    @Override
    public CancellationResult load(final Path directory,
                                   final ResourceManager manager,
                                   final ShaderProgram program,
                                   final ShaderObject object) {
        final var fingerprint = getFingerprint(program, object);
        final var file = directory.resolve(String.format("%s.glsl", fingerprint));
        if (Files.exists(file)) {
            PDAMod.LOGGER.debug(LogMarkers.RENDERER, "Shader cache hit for {}", object.getLocation());
            GL20.glShaderSource(object.getId(), loadText(file));
            return CancellationResult.CANCEL_NONE;
        }
        PDAMod.LOGGER.debug(LogMarkers.RENDERER, "Shader cache miss for {}", object.getLocation());
        GL20.glShaderSource(object.getId(), loadAndProcessSource(manager, program, object));
        return CancellationResult.CANCEL_NONE;
    }
}
