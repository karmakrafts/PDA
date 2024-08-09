/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.shader;

import io.karma.pda.api.client.render.shader.ShaderObject;
import io.karma.pda.api.client.render.shader.ShaderPreProcessor;
import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.client.render.shader.ShaderType;
import io.karma.pda.api.util.Exceptions;
import io.karma.pda.mod.PDAMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Alexander Hinze
 * @since 02/06/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultShaderObject implements ShaderObject {
    private final ShaderType type;
    private final int id;
    private final ResourceLocation location;
    private final Supplier<ShaderPreProcessor> shaderPreProcessorSupplier;
    private boolean isCompiled;

    DefaultShaderObject(final ShaderType type, final ResourceLocation location,
                        final Supplier<ShaderPreProcessor> shaderPreProcessorSupplier) {
        this.type = type;
        id = GL20.glCreateShader(type.getGlType());
        this.location = location;
        this.shaderPreProcessorSupplier = shaderPreProcessorSupplier;
    }

    void recompile(final ResourceProvider provider) {
        isCompiled = false;
        try (final var reader = provider.openAsReader(location)) {
            final var source = shaderPreProcessorSupplier.get().process(location.toString(),
                reader.lines().collect(Collectors.joining("\n")));
            PDAMod.LOGGER.debug("Processed shader source for {}:\n{}", location, source);
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

    @Override
    public int getId() {
        return id;
    }

    @Override
    public ResourceLocation getLocation() {
        return location;
    }

    @Override
    public ShaderType getType() {
        return type;
    }

    @Override
    public boolean isCompiled() {
        return isCompiled;
    }

    @Override
    public void onBindProgram(final ShaderProgram program) {
    }

    @Override
    public void onUnbindProgram(final ShaderProgram program) {
    }
}
