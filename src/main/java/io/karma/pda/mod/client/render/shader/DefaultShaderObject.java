/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.shader;

import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.shaders.Shader;
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
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Alexander Hinze
 * @since 02/06/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultShaderObject extends Program implements ShaderObject {
    private final ShaderType type;
    private final ResourceLocation location;
    private final Supplier<ShaderPreProcessor> shaderPreProcessorSupplier;
    private boolean isCompiled;
    private boolean isAttached;

    DefaultShaderObject(final ShaderType type, final ResourceLocation location,
                        final Supplier<ShaderPreProcessor> shaderPreProcessorSupplier) {
        super(getType(type), GL20.glCreateShader(type.getGlType()), location.toString());
        this.type = type;
        this.location = location;
        this.shaderPreProcessorSupplier = shaderPreProcessorSupplier;
    }

    private static Type getType(final ShaderType type) {
        return switch (type) {
            case VERTEX -> Type.VERTEX;
            case FRAGMENT -> Type.FRAGMENT;
        };
    }

    private static String loadSource(final ResourceProvider provider, final ResourceLocation location) {
        try (final var reader = provider.openAsReader(location)) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error("Could not load shader source {}: {}", location, Exceptions.toFancyString(error));
            return "";
        }
    }

    @Override
    public void attachToShader(final @NotNull Shader shader) {
    }

    @Override
    public void close() {
    }

    void recompile(final ShaderProgram program, final ResourceProvider provider) {
        isCompiled = false;
        PDAMod.LOGGER.debug("Processing shader source for {}", location);
        final var unprocessedSource = loadSource(provider, location);
        final var source = shaderPreProcessorSupplier.get().process(unprocessedSource,
            program,
            this,
            subLocation -> loadSource(provider, subLocation));
        PDAMod.LOGGER.debug("Processed shader source for {}", location);
        detach(program);
        GL20.glShaderSource(id, source);
        GL20.glCompileShader(id);
        if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            PDAMod.LOGGER.error("Could not recompile shader {}: {}", location, GL20.glGetShaderInfoLog(id));
            return;
        }
        isCompiled = true;
        attach(program);
        PDAMod.LOGGER.debug("Compiled shader object {} for program {}", location, program.getId());
    }

    @Override
    public void attach(final ShaderProgram program) {
        if (isAttached) {
            return;
        }
        GL20.glAttachShader(program.getId(), id);
        isAttached = true;
    }

    @Override
    public void detach(final ShaderProgram program) {
        if (!isAttached) {
            return;
        }
        GL20.glDetachShader(program.getId(), id);
        isAttached = false;
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

    @Override
    public String toString() {
        return String.format("DefaultShaderObject[id=%d,location=%s]", id, location);
    }
}
