/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.theme.font;

/**
 * @author Alexander Hinze
 * @since 28/04/2024
 */
public interface FontSet {
    static FontSet of(final Font heading1, final Font heading2, final Font heading3, final Font text) {
        return new DefaultFontSet(heading1, heading2, heading3, text);
    }

    static FontSet of(final Font headings, final Font text) {
        return new DefaultFontSet(headings, headings, headings, text);
    }

    static FontSet of(final Font font) {
        return new DefaultFontSet(font, font, font, font);
    }

    Font getHeading1();

    Font getHeading2();

    Font getHeading3();

    Font getText();
}
