/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.theme.font;

/**
 * @author Alexander Hinze
 * @since 28/04/2024
 */
final class DefaultFontSet implements FontSet {
    private final Font heading1;
    private final Font heading2;
    private final Font heading3;
    private final Font text;

    DefaultFontSet(final Font heading1, final Font heading2, final Font heading3, final Font text) {
        this.heading1 = heading1;
        this.heading2 = heading2;
        this.heading3 = heading3;
        this.text = text;
    }

    @Override
    public Font getHeading1() {
        return heading1;
    }

    @Override
    public Font getHeading2() {
        return heading2;
    }

    @Override
    public Font getHeading3() {
        return heading3;
    }

    @Override
    public Font getText() {
        return text;
    }
}
