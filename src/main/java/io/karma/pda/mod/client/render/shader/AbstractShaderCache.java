/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.shader;

import io.karma.pda.api.client.render.shader.ShaderCache;
import io.karma.pda.api.client.render.shader.ShaderObject;
import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.util.HashUtils;
import io.karma.pda.mod.PDAMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

/**
 * @author Alexander Hinze
 * @since 27/08/2024
 */
@OnlyIn(Dist.CLIENT)
public abstract class AbstractShaderCache implements ShaderCache {
    protected static String getFingerprint(final ShaderProgram program, final ShaderObject object) {
        return HashUtils.toFingerprint(program.hashCode(), object.hashCode());
    }

    protected static String loadSource(final ResourceManager manager, final ResourceLocation location) {
        try (final var reader = manager.openAsReader(location)) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error("Could not load shader source", error);
            return "";
        }
    }

    protected static String loadAndProcessSource(final ResourceManager manager,
                                                 final ShaderProgram program,
                                                 final ShaderObject object) {
        return object.getPreProcessor().process(loadSource(manager, object.getLocation()),
            program,
            object,
            location -> loadSource(manager, location));
    }

    protected static String loadText(final Path path) {
        try (final var reader = Files.newBufferedReader(path)) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error("Could not load file", error);
            return "";
        }
    }

    protected static void saveText(final Path path, final String text) {
        try {
            Files.deleteIfExists(path);
            try (final var writer = Files.newBufferedWriter(path)) {
                writer.write(text);
            }
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error("Could not save file", error);
        }
    }
}
