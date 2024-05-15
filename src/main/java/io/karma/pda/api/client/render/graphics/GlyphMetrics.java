/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.graphics;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 10/05/2024
 */
@OnlyIn(Dist.CLIENT)
public interface GlyphMetrics {
    int getWidth();

    int getHeight();

    int getAscent();

    int getDescent();

    int getLineGap();

    int getAdvance();

    int getLeftSideBearing();
}
