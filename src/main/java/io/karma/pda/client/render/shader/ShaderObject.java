/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.shader;

import io.karma.pda.api.common.util.Exceptions;
import io.karma.pda.common.PDAMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.stream.Collectors;

/**
 * @author Alexander Hinze
 * @since 02/06/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ShaderObject {
    private final int id;
    private final ResourceLocation location;
    private boolean isCompiled;

    public ShaderObject(final ShaderType type, final ResourceLocation location) {
        id = type.create();
        this.location = location;
    }

    void recompile(final ResourceProvider provider) {
        isCompiled = false;
        try (final var reader = provider.openAsReader(location)) {
            final var source = reader.lines().collect(Collectors.joining("\n"));
            GL20.glShaderSource(id, source);
            GL20.glCompileShader(id);
            if (GL11.glGetInteger(GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                final var length = GL11.glGetInteger(GL20.GL_INFO_LOG_LENGTH);
                final var log = GL20.glGetShaderInfoLog(id, length);
                PDAMod.LOGGER.error("Could not recompile shader {}: {}", location, log);
                return;
            }
            isCompiled = true;
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error("Could not load shader {}: {}", location, Exceptions.toFancyString(error));
        }
    }

    public boolean isCompiled() {
        return isCompiled;
    }

    public int getId() {
        return id;
    }

    public ResourceLocation getLocation() {
        return location;
    }
}
