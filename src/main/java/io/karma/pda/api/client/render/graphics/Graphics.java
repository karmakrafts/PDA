/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.graphics;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.karma.pda.api.color.ColorProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.IntFunction;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public interface Graphics {
    void flush();

    GraphicsContext getContext();

    Graphics copyWithContext(final GraphicsContext context);

    default Graphics copy() {
        return copyWithContext(getContext());
    }

    GraphicsState getState();

    GraphicsState pushState();

    void popState();

    void point(final int x, final int y);

    void line(final int startX, final int startY, final int endX, final int endY);

    void hLine(final int startX, final int endX, final int y);

    void vLine(final int x, final int startY, final int endY);

    void drawRect(final int x, final int y, final int width, final int height);

    void fillRect(final int x, final int y, final int width, final int height);

    void drawCircle(final int x, final int y, final int radius);

    void fillCircle(final int x, final int y, final int radius);

    void drawTriangle(final int x1, final int y1, final int x2, final int y2, final int x3, final int y3);

    void fillTriangle(final int x1, final int y1, final int x2, final int y2, final int x3, final int y3);

    void text(final int x, final int y, final char c);

    void text(final int x, final int y, final CharSequence text);

    void text(final int x, final int y, final int maxWidth, final CharSequence text);

    void text(final int x, final int y, final int maxWidth, final int maxHeight, final CharSequence text);

    void text(final int x, final int y, final int maxWidth, final CharSequence text, final CharSequence cutoffSuffix);

    void text(final int x, final int y, final int maxWidth, final int maxHeight, final CharSequence text,
              final CharSequence cutoffSuffix);

    // Extended text rendering with per-glyph colors

    void text(final int x, final int y, final CharSequence text, final IntFunction<ColorProvider> color);

    void text(final int x, final int y, final int maxWidth, final CharSequence text,
              final IntFunction<ColorProvider> color);

    void text(final int x, final int y, final int maxWidth, final int maxHeight, final CharSequence text,
              final IntFunction<ColorProvider> color);

    void text(final int x, final int y, final int maxWidth, final CharSequence text, final CharSequence cutoffSuffix,
              final IntFunction<ColorProvider> color);

    void text(final int x, final int y, final int maxWidth, final int maxHeight, final CharSequence text,
              final CharSequence cutoffSuffix, final IntFunction<ColorProvider> color);

    default VertexConsumer getBuffer() {
        return getContext().getBufferSource().getBuffer(getState().getBrush().getRenderType(getContext().getDisplayMode()));
    }
}
