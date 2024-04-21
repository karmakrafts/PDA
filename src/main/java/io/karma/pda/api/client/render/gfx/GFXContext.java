/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.gfx;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public interface GFXContext {
    PoseStack getPoseStack();

    MultiBufferSource getBufferSource();

    int getWidth();

    int getHeight();

    default Matrix4f getTransform() {
        return getPoseStack().last().pose();
    }

    default boolean isDebugMode() {
        return Minecraft.getInstance().options.renderDebug;
    }
}
