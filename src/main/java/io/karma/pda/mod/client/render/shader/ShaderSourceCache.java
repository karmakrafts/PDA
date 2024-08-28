/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.shader;

import io.karma.pda.api.client.render.shader.ShaderObject;
import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.util.HashUtils;
import io.karma.pda.api.util.IOUtils;
import io.karma.pda.api.util.LogMarkers;
import io.karma.pda.mod.PDAMod;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL20;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

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
        try {
            final var fingerprint = getFingerprint(program, object);
            final var file = directory.resolve(String.format("%s.glsl", fingerprint));
            IOUtils.deleteIfExists(file);
            saveText(file, GL20.glGetShaderSource(object.getId()));
            final var fingerprintFile = directory.resolve(String.format("%s.md5", fingerprint));
            IOUtils.deleteIfExists(fingerprintFile);
            saveText(fingerprintFile, HashUtils.toFingerprint(loadSource(manager, object.getLocation())));
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error("Could not save processed shader source", error);
        }
    }

    @Override
    public CancellationResult load(final Path directory,
                                   final ResourceManager manager,
                                   final ShaderProgram program,
                                   final ShaderObject object) {
        final var fingerprint = getFingerprint(program, object);
        final var file = directory.resolve(String.format("%s.glsl", fingerprint));
        final var fingerprintFile = directory.resolve(String.format("%s.md5", fingerprint));
        final var location = object.getLocation();
        final var source = loadSource(manager, location);
        if (Files.exists(file)) {
            if (!Files.exists(fingerprintFile)) {
                PDAMod.LOGGER.error("Shader {} missing fingerprint file, aborting", location);
                return CancellationResult.CANCEL_LINK;
            }
            final var previousFingerprint = loadText(fingerprintFile);
            if (Objects.requireNonNull(HashUtils.toFingerprint(source)).equals(previousFingerprint)) {
                PDAMod.LOGGER.debug(LogMarkers.RENDERER, "Shader cache hit for {}", location);
                GL20.glShaderSource(object.getId(), loadText(file));
                return CancellationResult.CANCEL_NONE;
            }
            else {
                try {
                    PDAMod.LOGGER.debug("Invalidating shader source cache entry {} for object {}",
                        fingerprint,
                        location);
                    IOUtils.deleteIfExists(file);
                    IOUtils.deleteIfExists(fingerprintFile);
                }
                catch (Throwable error) {
                    PDAMod.LOGGER.error("Could not delete resident shader source cache files", error);
                }
            }
        }
        PDAMod.LOGGER.debug(LogMarkers.RENDERER, "Shader cache miss for {}", location);
        GL20.glShaderSource(object.getId(),
            object.getPreProcessor().process(source, program, object, loc -> loadSource(manager, loc)));
        return CancellationResult.CANCEL_NONE;
    }
}
