/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.app.theme.font;

import io.karma.pda.api.app.theme.font.FontCharSet;
import it.unimi.dsi.fastutil.ints.IntIntPair;

/**
 * @author Alexander Hinze
 * @since 05/06/2024
 */
public enum DefaultCharSet implements FontCharSet {
    // @formatter:off
    ASCII         (IntIntPair.of(0x20, 0x7E)),
    EXTENDED_ASCII(IntIntPair.of(0x20, 0x7E), IntIntPair.of(0xA0, 0xFF)),
    UNICODE       (
        IntIntPair.of(0x0020, 0x007E), // ASCII
        IntIntPair.of(0x00A0, 0x00FF), // Extended ASCII
        IntIntPair.of(0x0100, 0x017F), // Latin Extended-A
        IntIntPair.of(0x0370, 0x03FF), // Greek and Coptic
        IntIntPair.of(0x0400, 0x04FF), // Cyrillic
        IntIntPair.of(0x0590, 0x05FF), // Hebrew
        IntIntPair.of(0x0600, 0x06FF), // Arabic
        IntIntPair.of(0x0900, 0x097F), // Devanagari
        IntIntPair.of(0x4E00, 0x9FFF)  // Chinese, Japanese, Korean (CJK) Unified Ideographs
    );
    // @formatter:on

    private final IntIntPair[] ranges;

    DefaultCharSet(final IntIntPair... ranges) {
        this.ranges = ranges;
    }

    @Override
    public IntIntPair[] getRanges() {
        return ranges;
    }
}
