/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.graphics;

import io.karma.pda.api.app.theme.font.Font;
import io.karma.pda.api.color.ColorProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.IntFunction;

/**
 * @author Alexander Hinze
 * @since 04/05/2024
 */
@OnlyIn(Dist.CLIENT)
public interface FontRenderer {
    int getLineHeight(final Font font);

    FontAtlas getFontAtlas(final Font font);

    int getStringWidth(final Font font, final CharSequence s);

    int render(final int x, final int y, final char c, final Font font, final ColorProvider color);

    int render(final int x, final int y, final CharSequence text, final Font font, final ColorProvider color);

    int render(final int x,
               final int y,
               final int maxWidth,
               final CharSequence text,
               final Font font,
               final ColorProvider color);

    int render(final int x,
               final int y,
               final int maxWidth,
               final int maxHeight,
               final CharSequence text,
               final Font font,
               final ColorProvider color);

    int render(final int x,
               final int y,
               final int maxWidth,
               final CharSequence text,
               final CharSequence cutoffSuffix,
               final Font font,
               final ColorProvider color);

    int render(final int x,
               final int y,
               final int maxWidth,
               final int maxHeight,
               final CharSequence text,
               final CharSequence cutoffSuffix,
               final Font font,
               final ColorProvider color);

    // Versions that allow per-glyph colors

    int render(final int x,
               final int y,
               final CharSequence text,
               final Font font,
               final IntFunction<ColorProvider> color);

    int render(final int x,
               final int y,
               final int maxWidth,
               final CharSequence text,
               final Font font,
               final IntFunction<ColorProvider> color);

    int render(final int x,
               final int y,
               final int maxWidth,
               final int maxHeight,
               final CharSequence text,
               final Font font,
               final IntFunction<ColorProvider> color);

    int render(final int x,
               final int y,
               final int maxWidth,
               final CharSequence text,
               final CharSequence cutoffSuffix,
               final Font font,
               final IntFunction<ColorProvider> color);

    int render(final int x,
               final int y,
               final int maxWidth,
               final int maxHeight,
               final CharSequence text,
               final CharSequence cutoffSuffix,
               final Font font,
               final IntFunction<ColorProvider> color);
}
