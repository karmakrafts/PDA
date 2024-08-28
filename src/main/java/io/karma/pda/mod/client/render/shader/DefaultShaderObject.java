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
import io.karma.pda.api.util.HashUtils;
import io.karma.pda.api.util.LogMarkers;
import io.karma.pda.mod.PDAMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.nio.file.Path;
import java.util.function.Supplier;

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

    DefaultShaderObject(final ShaderType type,
                        final ResourceLocation location,
                        final Supplier<ShaderPreProcessor> shaderPreProcessorSupplier) {
        super(getType(type), GL20.glCreateShader(type.getGlType()), location.toString());
        this.type = type;
        this.location = location;
        this.shaderPreProcessorSupplier = shaderPreProcessorSupplier;
        PDAMod.LOGGER.debug(LogMarkers.RENDERER, "Created new shader object {}", id);
    }

    private static Type getType(final ShaderType type) {
        return switch (type) {
            case VERTEX -> Type.VERTEX;
            case FRAGMENT -> Type.FRAGMENT;
        };
    }

    boolean reload(final Path directory, final ShaderProgram program, final ResourceManager manager) {
        final var shaderCache = program.getCache();

        final var cacheResult = shaderCache.load(directory, manager, program, this);
        if (cacheResult.cancelCompile()) {
            isCompiled = true;
            return !cacheResult.cancelLink();
        }

        PDAMod.LOGGER.debug(LogMarkers.RENDERER, "Compiling shader {}", location);

        detach(program);
        GL20.glCompileShader(id);
        if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            PDAMod.LOGGER.error(LogMarkers.RENDERER,
                "Could not recompile shader {}: {}",
                location,
                GL20.glGetShaderInfoLog(id));
            return false; // Link is always cancelled in this case
        }
        isCompiled = true;
        attach(program);
        shaderCache.save(directory, manager, program, this);

        PDAMod.LOGGER.debug(LogMarkers.RENDERER, "Compiled shader {} for program {}", location, program.getId());
        return true; // Link may occur
    }

    @Override
    public ShaderPreProcessor getPreProcessor() {
        return shaderPreProcessorSupplier.get();
    }

    @Override
    public void attachToShader(final @NotNull Shader shader) {
    }

    @Override
    public void close() {
    }

    @Override
    public void attach(final ShaderProgram program) {
        if (program.isAttached(this)) {
            return;
        }
        GL20.glAttachShader(program.getId(), id);
    }

    @Override
    public void detach(final ShaderProgram program) {
        if (!program.isAttached(this)) {
            return;
        }
        GL20.glDetachShader(program.getId(), id);
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
    public String toString() {
        return String.format("DefaultShaderObject[id=%d,location=%s]", id, location);
    }

    @Override
    public int hashCode() {
        return HashUtils.combine(type.ordinal(), location.hashCode());
    }
}
