/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.display;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexSorting;
import io.karma.pda.api.client.render.display.DisplayBlitter;
import io.karma.pda.api.client.render.display.DisplayMode;
import io.karma.pda.api.client.render.display.Framebuffer;
import io.karma.pda.api.display.DisplayModeSpec;
import io.karma.pda.api.display.DisplayResolution;
import io.karma.pda.api.util.FloatSupplier;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 02/06/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultDisplayMode implements DisplayMode {
    private static final ModeCache CACHE = new ModeCache();
    private static final Matrix4f IDENTITY_MATRIX = new Matrix4f().identity();

    private final DisplayModeSpec spec;
    private final Framebuffer framebuffer;
    private final Matrix4f projectionMatrix;
    private final DefaultDisplayBlitter blitter;
    private final RenderStateShard.OutputStateShard outputState;

    public DefaultDisplayMode(final DisplayModeSpec spec, final Framebuffer framebuffer,
                              final FloatSupplier glitchFactorSupplier) {
        this.spec = spec;
        this.framebuffer = framebuffer;
        final var resolution = spec.resolution();
        outputState = new RenderStateShard.OutputStateShard(spec.name(), this::setup, this::restore);
        projectionMatrix = new Matrix4f().ortho2D(0F, resolution.getWidth(), resolution.getHeight(), 0F);
        blitter = new DefaultDisplayBlitter(this, glitchFactorSupplier);
    }

    private void setup() {
        framebuffer.bind();

        CACHE.projectionMatrix = RenderSystem.getProjectionMatrix();
        CACHE.vertexSorting = RenderSystem.getVertexSorting();
        RenderSystem.setProjectionMatrix(projectionMatrix, VertexSorting.ORTHOGRAPHIC_Z);
        CACHE.modelViewMatrix = RenderSystem.modelViewMatrix;
        RenderSystem.modelViewMatrix = IDENTITY_MATRIX;

        GL11.glGetIntegerv(GL11.GL_VIEWPORT, CACHE.viewport);
        final var resolution = spec.resolution();
        GL11.glViewport(0, 0, resolution.getWidth(), resolution.getHeight());
        CACHE.frontFace = GL11.glGetInteger(GL11.GL_FRONT_FACE);
        GL11.glFrontFace(GL11.GL_CW);
    }

    private void restore() {
        GL11.glFrontFace(CACHE.frontFace);
        GL11.glViewport(CACHE.viewport[0], CACHE.viewport[1], CACHE.viewport[2], CACHE.viewport[3]);
        RenderSystem.setProjectionMatrix(CACHE.projectionMatrix, CACHE.vertexSorting);
        RenderSystem.modelViewMatrix = CACHE.modelViewMatrix;
        framebuffer.unbind();
    }

    @Override
    public DisplayModeSpec getSpec() {
        return spec;
    }

    @Override
    public DisplayResolution getResolution() {
        return spec.resolution();
    }

    @Override
    public Framebuffer getFramebuffer() {
        return framebuffer;
    }

    @Override
    public DisplayBlitter getBlitter() {
        return blitter;
    }

    @Override
    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    @Override
    public RenderStateShard.OutputStateShard getOutputState() {
        return outputState;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof DefaultDisplayMode mode)) {
            return false;
        }
        return mode.spec.equals(spec) && mode.framebuffer == framebuffer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(spec, framebuffer);
    }

    @Override
    public String toString() {
        return spec.toString();
    }

    private static final class ModeCache {
        final int[] viewport = new int[4];
        Matrix4f projectionMatrix;
        VertexSorting vertexSorting;
        Matrix4f modelViewMatrix;
        int frontFace;
    }
}
