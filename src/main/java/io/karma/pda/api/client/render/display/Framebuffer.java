/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.display;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 03/06/2024
 */
@OnlyIn(Dist.CLIENT)
public interface Framebuffer {
    void bind();

    void unbind();

    int getColorTexture();

    int getDepthTexture();

    void clear(final float r, final float g, final float b, final float a);

    int getWidth();

    int getHeight();
}
