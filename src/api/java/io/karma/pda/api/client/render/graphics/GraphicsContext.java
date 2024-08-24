/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.graphics;

import com.mojang.blaze3d.vertex.PoseStack;
import io.karma.pda.api.client.render.display.DisplayMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
// TODO: document this
@OnlyIn(Dist.CLIENT)
public interface GraphicsContext {
    FontRenderer getFontRenderer();

    Graphics getGraphics();

    int getDefaultZIndex();

    PoseStack getPoseStack();

    MultiBufferSource getBufferSource();

    DisplayMode getDisplayMode(); // Contains the total display dimensions

    int getWidth(); // The actual width of the context viewport

    int getHeight(); // The actual width of the context viewport

    boolean isDebugMode();

    GraphicsContext derive(final int width, final int height);

    BrushFactory getBrushFactory();

    default Matrix4f getTransform() {
        return getPoseStack().last().pose();
    }
}
