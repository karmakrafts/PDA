/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

import com.mojang.blaze3d.vertex.PoseStack;
import io.karma.pda.api.client.render.graphics.GraphicsContext;
import io.karma.pda.common.PDAMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultGraphicsContext implements GraphicsContext {
    private PoseStack poseStack;
    private MultiBufferSource bufferSource;
    private int width;
    private int height;

    public void setup(final PoseStack poseStack, final MultiBufferSource bufferSource, final int width,
                      final int height) {
        this.poseStack = poseStack;
        this.bufferSource = bufferSource;
        this.width = width;
        this.height = height;
    }

    @Override
    public GraphicsContext derive(final int width, final int height) {
        final var context = new DefaultGraphicsContext();
        context.setup(poseStack, bufferSource, width, height);
        return context;
    }

    @Override
    public PoseStack getPoseStack() {
        return poseStack;
    }

    @Override
    public MultiBufferSource getBufferSource() {
        return bufferSource;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public boolean isDebugMode() {
        return PDAMod.IS_DEV_ENV && Minecraft.getInstance().options.renderDebug;
    }
}
