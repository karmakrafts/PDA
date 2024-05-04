/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.graphics;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public interface GraphicsContext {
    int getDefaultZIndex();

    PoseStack getPoseStack();

    MultiBufferSource getBufferSource();

    int getWidth();

    int getHeight();

    boolean isDebugMode();

    GraphicsContext derive(final int width, final int height);

    BrushFactory getBrushFactory();

    FontRenderer getFontRenderer();

    default Matrix4f getTransform() {
        return getPoseStack().last().pose();
    }
}
