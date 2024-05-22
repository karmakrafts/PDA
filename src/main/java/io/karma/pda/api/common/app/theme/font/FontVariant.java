/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.theme.font;

/**
 * @author Alexander Hinze
 * @since 20/05/2024
 */
public interface FontVariant extends Font {
    float DEFAULT_SIZE = 16F;

    FontStyle getStyle();

    float getSize();

    FontVariant derive(final FontStyle style);

    FontVariant derive(final float size);

    default FontVariant derive(final FontStyle style, final float size) {
        return derive(style).derive(size);
    }
}
