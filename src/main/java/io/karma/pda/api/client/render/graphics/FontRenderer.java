/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.graphics;

import io.karma.pda.api.common.app.theme.font.Font;
import io.karma.pda.api.common.color.ColorProvider;
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

    int render(final int x, final int y, final int zIndex, final char c, final ColorProvider colorProvider,
               final Font font, final GraphicsContext context);

    int render(final int x, final int y, final int zIndex, final CharSequence s, final ColorProvider colorProvider,
               final Font font, final GraphicsContext context);

    int render(final int x, final int y, final int zIndex, final CharSequence s,
               final IntFunction<ColorProvider> colorFunction, final Font font, final GraphicsContext context);
}
