/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.gfx;

import com.mojang.blaze3d.vertex.PoseStack;
import io.karma.pda.api.client.render.gfx.GFXContext;
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
public final class DefaultGFXContext implements GFXContext {
    private final PoseStack poseStack;
    private final MultiBufferSource bufferSource;
    private final int width;
    private final int height;

    public DefaultGFXContext(final PoseStack poseStack, final MultiBufferSource bufferSource, final int width,
                             final int height) {
        this.poseStack = poseStack;
        this.bufferSource = bufferSource;
        this.width = width;
        this.height = height;
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
