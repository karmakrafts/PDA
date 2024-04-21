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

    BrushFactory getBrushFactory();

    GFX copy();

    void setBrush(final Brush brush);

    Brush getBrush();

    void setZIndex(final int index);

    int getZIndex();

    void point(final int x, final int y);

    void line(final int startX, final int startY, final int endX, final int endY);

    void hLine(final int startX, final int endX, final int y);

    void vLine(final int x, final int startY, final int endY);

    void drawRect(final int x, final int y, final int width, final int height);

    void fillRect(final int x, final int y, final int width, final int height);

    void drawCircle(final int x, final int y, final int radius);

    void fillCircle(final int x, final int y, final int radius);

    void drawRoundedRect(final int x, final int y, final int width, final int height, final float rounding);

    void fillRoundedRect(final int x, final int y, final int width, final int height, final float rounding);

    void drawTriangle(final int x1, final int y1, final int x2, final int y2, final int x3, final int y3);

    void fillTriangle(final int x1, final int y1, final int x2, final int y2, final int x3, final int y3);

    void text(final int x, final int y, final String text, final int maxLength, final String delimiter);

    default void text(final int x, final int y, final String text, final String delimiter) {
        text(x, y, text, text.length(), delimiter);
    }

    default void text(final int x, final int y, final String text) {
        text(x, y, text, text.length(), "...");
    }

    void wrappedText(final int x, final int y, final String text, final int maxLength);
}
