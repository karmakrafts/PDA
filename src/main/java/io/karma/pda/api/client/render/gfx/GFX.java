/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.gfx;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public interface GFX {
    GFXContext getContext();

    GFX copy();

    void setZIndex(final int index);

    int getZIndex();

    void point(final int x, final int y, final int color);

    void line(final int startX, final int startY, final int endX, final int endY, final int color);

    void drawRect(final int x, final int y, final int width, final int height, final int color);

    void fillRect(final int x, final int y, final int width, final int height, final int color);

    void drawCircle(final int x, final int y, final int radius, final int color);

    void fillCircle(final int x, final int y, final int radius, final int color);

    void drawRoundedRect(final int x, final int y, final int width, final int height, final int color,
                         final float rounding);

    void fillRoundedRect(final int x, final int y, final int width, final int height, final int color,
                         final float rounding);

    void drawTriangle(final int x1, final int y1, final int x2, final int y2, final int x3, final int y3,
                      final int color);

    void fillTriangle(final int x1, final int y1, final int x2, final int y2, final int x3, final int y3,
                      final int color);
}
