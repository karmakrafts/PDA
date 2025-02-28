/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.shader;

import io.karma.pda.api.client.render.shader.Sampler;
import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.util.LogMarkers;
import io.karma.pda.mod.PDAMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import java.util.function.IntSupplier;

/**
 * @author Alexander Hinze
 * @since 13/08/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DynamicSampler implements Sampler {
    private final int id;
    private final String name;
    private IntSupplier textureId = () -> -1;

    public DynamicSampler(final int id, final String name) {
        this.id = id;
        this.name = name;
    }

    public void setTextureId(final IntSupplier textureId) {
        this.textureId = textureId;
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
            "Creating dynamic sampler '{}'/{} for program {}",
            name,
            id,
            program.getId());
        GL20.glUseProgram(program.getId());
        GL20.glUniform1i(program.getUniformLocation(name), id);
        GL20.glUseProgram(0);
    }

    @Override
    public void bind(final ShaderProgram program) {
        final var textureId = this.textureId.getAsInt();
        if (textureId <= 0) {
            return;
        }
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + id);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
    }

    @Override
    public void unbind(final ShaderProgram program) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + id);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
    }

    @Override
    public String toString() {
        return String.format("DynamicSampler[textureId=%d]", textureId.getAsInt());
    }
}
