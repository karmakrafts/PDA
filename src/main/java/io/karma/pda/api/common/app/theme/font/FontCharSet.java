/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.theme.font;

import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntIntPair;

/**
 * @author Alexander Hinze
 * @since 05/05/2024
 */
public interface FontCharSet {
    IntIntPair[] getRanges();

    default int getCharCount() {
        var charCount = 0;
        for (final var range : getRanges()) {
            charCount += range.rightInt() - range.leftInt();
        }
        return charCount;
    }

    default char[] toArray() {
        final var chars = new CharArrayList(getCharCount());
        for (final var range : getRanges()) {
            for (var i = range.leftInt(); i <= range.rightInt(); i++) {
                chars.add((char) i);
            }
        }
        return chars.toCharArray();
    }

    default CharOpenHashSet toSet() {
        final var set = new CharOpenHashSet(getCharCount());
        for (final var range : getRanges()) {
            for (var i = range.leftInt(); i <= range.rightInt(); i++) {
                set.add((char) i);
            }
        }
        return set;
    }
}
