/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.display;

import io.karma.pda.api.display.DisplayModeSpec;
import io.karma.pda.api.display.DisplayResolution;
import io.karma.peregrine.api.framebuffer.Framebuffer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

/**
 * @author Alexander Hinze
 * @since 03/06/2024
 */
@OnlyIn(Dist.CLIENT)
public interface DisplayMode {
    DisplayModeSpec getSpec();

    DisplayResolution getResolution();

    DisplayBlitter getBlitter();

    /**
     * Retrieves the framebuffer instance associated with this display mode's resolution.
     * The framebuffer returned by this function must not be unique to this display mode.
     *
     * @return The framebuffer instance associated with this display mode.
     */
    Framebuffer getFramebuffer();

    Matrix4f getProjectionMatrix();

    RenderStateShard.OutputStateShard getOutputState();
}
