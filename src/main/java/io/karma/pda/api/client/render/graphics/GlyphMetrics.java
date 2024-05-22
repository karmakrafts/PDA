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
    float getWidth();

    float getHeight();

    float getAscent();

    float getDescent();

    float getAdvanceX();

    float getAdvanceY();

    float getBearingX();

    float getBearingY();
}
