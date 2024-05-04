/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.graphics;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 04/05/2024
 */
@OnlyIn(Dist.CLIENT)
public interface GlyphSprite {
    int getWidth();

    int getHeight();

    float getU();

    float getV();
}
