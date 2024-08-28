/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.shader;

import io.karma.pda.api.client.render.shader.ShaderObject;
import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.util.HashUtils;
import io.karma.pda.api.util.LogMarkers;
import io.karma.pda.mod.PDAMod;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.ARBGetProgramBinary;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.BufferedInputStream;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Alexander Hinze
 * @since 26/08/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ShaderBinaryCache extends AbstractShaderCache {
    public static final boolean IS_SUPPORTED = GL.getCapabilities().GL_ARB_get_program_binary;
    private static int binaryFormat;

    static {
        if (IS_SUPPORTED) {
            PDAMod.LOGGER.info(LogMarkers.RENDERER,
                "Detected GL_ARB_get_program_binary support, enabling shader binary caching");
            try (final var stack = MemoryStack.stackPush()) {
                final var formatCount = GL11.glGetInteger(ARBGetProgramBinary.GL_NUM_PROGRAM_BINARY_FORMATS);
                final var formats = stack.mallocInt(formatCount);
                GL11.glGetIntegerv(ARBGetProgramBinary.GL_PROGRAM_BINARY_FORMATS, formats);
                binaryFormat = formats.get(0);
                PDAMod.LOGGER.debug(LogMarkers.RENDERER,
                    "Using shader binary format 0x{}",
                    Integer.toHexString(binaryFormat));
            }
        }
        else {
            PDAMod.LOGGER.info(LogMarkers.RENDERER,
                "Detected no GL_ARB_get_program_binary support, disabling shader binary caching");
        }
    }

    ShaderBinaryCache() {
        PDAMod.LOGGER.debug(LogMarkers.RENDERER, "Creating binary shader cache");
    }

    @Override
    public void prepareProgram(final ShaderProgram program) {
        ARBGetProgramBinary.glProgramParameteri(program.getId(),
            ARBGetProgramBinary.GL_PROGRAM_BINARY_RETRIEVABLE_HINT,
            GL11.GL_TRUE);
    }

    @Override
    public void saveProgram(final Path directory, final ResourceManager manager, final ShaderProgram program) {
        try {
            final var fingerprint = HashUtils.toFingerprint(program.hashCode());
            final var file = directory.resolve(String.format("%s.bin", fingerprint));
            Files.deleteIfExists(file);

            final var id = program.getId();
            if (GL20.glGetProgrami(id, ARBGetProgramBinary.GL_PROGRAM_BINARY_RETRIEVABLE_HINT) == GL11.GL_FALSE) {
                PDAMod.LOGGER.warn("Could not save shader program binary for program {}", id);
                return;
            }

            final var size = GL20.glGetProgrami(id, ARBGetProgramBinary.GL_PROGRAM_BINARY_LENGTH);
            final var data = MemoryUtil.memAlloc(size);

            try (final var stack = MemoryStack.stackPush()) {
                final var format = stack.mallocInt(1);
                ARBGetProgramBinary.glGetProgramBinary(id, null, format, data);
                if (format.get() != binaryFormat) {
                    PDAMod.LOGGER.warn(LogMarkers.RENDERER, "Mismatching shader program binary format");
                }
            }

            try (final var channel = Channels.newChannel(Files.newOutputStream(file))) {
                channel.write(data);
            }
            MemoryUtil.memFree(data);
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error("Could not save shader program binary", error);
        }
    }

    @Override
    public boolean loadProgram(final Path directory, final ResourceManager manager, final ShaderProgram program) {
        final var fingerprint = HashUtils.toFingerprint(program.hashCode());
        final var file = directory.resolve(String.format("%s.bin", fingerprint));
        if (!Files.exists(file)) {
            PDAMod.LOGGER.debug(LogMarkers.RENDERER, "Shader binary cache miss for program {}", program);
            return false;
        }
        try (final var stream = new BufferedInputStream(Files.newInputStream(file)); final var channel = Channels.newChannel(
            stream)) {
            final var size = stream.available();
            final var data = MemoryUtil.memAlloc(size);
            channel.read(data);
            data.flip();
            ARBGetProgramBinary.glProgramBinary(program.getId(), binaryFormat, data);
            MemoryUtil.memFree(data);
            PDAMod.LOGGER.debug(LogMarkers.RENDERER, "Shader binary cache hit for program {}", program);
            return true;
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error(LogMarkers.RENDERER, "Could not load shader program binary", error);
            return false;
        }
    }

    @Override
    public void save(final Path directory,
                     final ResourceManager manager,
                     final ShaderProgram program,
                     final ShaderObject object) {
    }

    @Override
    public CancellationResult load(final Path directory,
                                   final ResourceManager manager,
                                   final ShaderProgram program,
                                   final ShaderObject object) {
        GL20.glShaderSource(object.getId(), loadAndProcessSource(manager, program, object));
        return CancellationResult.CANCEL_NONE;
    }
}
