/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.shader;

import io.karma.pda.api.client.render.shader.Sampler;
import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.dispose.Disposable;
import io.karma.pda.api.dispose.DispositionPriority;
import io.karma.pda.api.reload.PrepareReloadPriority;
import io.karma.pda.api.reload.ReloadPriority;
import io.karma.pda.api.reload.Reloadable;
import io.karma.pda.api.util.LogMarkers;
import io.karma.pda.mod.PDAMod;
import io.karma.pda.mod.reload.DefaultReloadHandler;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.ARBBindlessTexture;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL20;

import java.util.function.IntSupplier;

/**
 * @author Alexander Hinze
 * @since 13/08/2024
 */
@OnlyIn(Dist.CLIENT)
@DispositionPriority(100)
@ReloadPriority(-100) // Reload after everything else
@PrepareReloadPriority(100) // Prepare before everything else
public final class StaticSampler implements Sampler, Disposable, Reloadable {
    public static final boolean IS_SUPPORTED;

    static {
        IS_SUPPORTED = GL.getCapabilities().GL_ARB_bindless_texture;
        if (IS_SUPPORTED) {
            PDAMod.LOGGER.info("Detected GL_ARB_bindless_texture support, enabling static samplers");
        }
        else {
            PDAMod.LOGGER.info("Detected no GL_ARB_bindless_texture support, disabling static samplers");
        }
    }

    private final int id;
    private final String name;
    private final IntSupplier textureId;
    private long textureHandle = -1;

    private StaticSampler(final int id, final String name, final IntSupplier textureId) {
        this.id = id;
        this.name = name;
        this.textureId = textureId;
        PDAMod.DISPOSITION_HANDLER.register(this);
        DefaultReloadHandler.INSTANCE.register(this);
    }

    /**
     * This handles creating static samplers with a safe fallback to {@link DynamicSampler}
     * if the GL_ARB_bindless_texture extension is not supported.
     *
     * @param textureId The texture ID to create a static sampler for.
     * @return A new instance of {@link StaticSampler} if the GL_ARB_bindless_texture extension is present,
     * a new instance of {@link DynamicSampler} with the given texture ID pre-setup otherwise.
     */
    public static Sampler create(final int id, final String name, final IntSupplier textureId) {
        if (!IS_SUPPORTED) {
            final var sampler = new DynamicSampler(id, name);
            sampler.setTextureId(textureId);
            return sampler;
        }
        return new StaticSampler(id, name, textureId);
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setup(final ShaderProgram program) {
        PDAMod.LOGGER.debug(LogMarkers.RENDERER,
            "Creating static sampler '{}'/{} for program {}",
            name,
            id,
            program.getId());

        final var textureId = this.textureId.getAsInt();
        if (textureId <= 0) {
            PDAMod.LOGGER.warn("Could not create static sampler '{}'/{} for program {}: invalid texture ID",
                name,
                id,
                program.getId());
            return;
        }

        textureHandle = ARBBindlessTexture.glGetTextureHandleARB(textureId);
        if (!ARBBindlessTexture.glIsTextureHandleResidentARB(textureHandle)) {
            ARBBindlessTexture.glMakeTextureHandleResidentARB(textureHandle);
        }

        final var location = program.getUniformLocation(name);
        GL20.glUseProgram(program.getId());
        ARBBindlessTexture.glUniformHandleui64ARB(location, textureHandle);
        GL20.glUseProgram(0);
    }

    @Override
    public void bind(final ShaderProgram program) {
    }

    @Override
    public void unbind(final ShaderProgram program) {
    }

    @Override
    public void dispose() {
        if (textureHandle == -1) {
            return;
        }
        if (ARBBindlessTexture.glIsTextureHandleResidentARB(textureHandle)) {
            ARBBindlessTexture.glMakeTextureHandleNonResidentARB(textureHandle);
        }
        textureHandle = -1;
    }

    @Override
    public void reload(final ResourceManager manager) {
    }

    @Override
    public void prepareReload(final ResourceManager manager) {
        dispose();
    }

    @Override
    public String toString() {
        return String.format("StaticSampler[textureId=%d,handle=%d]", textureId.getAsInt(), textureHandle);
    }
}
