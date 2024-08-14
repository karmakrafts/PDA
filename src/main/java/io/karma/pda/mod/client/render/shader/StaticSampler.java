/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.shader;

import io.karma.pda.api.client.render.shader.Sampler;
import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.dispose.Disposable;
import io.karma.pda.api.util.LogMarkers;
import io.karma.pda.mod.PDAMod;
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
public final class StaticSampler implements Sampler, Disposable {
    private static final boolean IS_SUPPORTED;

    static {
        if (IS_SUPPORTED = GL.getCapabilities().GL_ARB_bindless_texture) {
            PDAMod.LOGGER.info("Detected GL_ARB_bindless_texture support, enabling static samplers");
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
        PDAMod.DISPOSITION_HANDLER.addObject(this);
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

        if (textureHandle != -1) {
            ARBBindlessTexture.glMakeTextureHandleNonResidentARB(textureHandle);
        }
        textureHandle = ARBBindlessTexture.glGetTextureHandleARB(textureId);
        if (!ARBBindlessTexture.glIsTextureHandleResidentARB(textureHandle)) {
            ARBBindlessTexture.glMakeTextureHandleResidentARB(textureHandle);
        }

        final var location = program.getUniformCache().getLocation(name);
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
    public int getDispositionPriority() {
        return 1; // Samplers need to be freed before everything else
    }

    @Override
    public String toString() {
        return String.format("StaticSampler[textureId=%d,handle=%d]", textureId.getAsInt(), textureHandle);
    }
}
