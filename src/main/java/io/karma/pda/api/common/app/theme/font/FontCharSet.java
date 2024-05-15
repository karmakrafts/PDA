/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.theme.font;

import io.karma.pda.api.common.util.CharIntConsumer;
import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntIntPair;

/**
 * @author Alexander Hinze
 * @since 05/05/2024
 */
public interface FontCharSet {
    IntIntPair[] getRanges();

    default void forEachChar(final CharIntConsumer consumer) {
        var index = 0;
        for (final var range : getRanges()) {
            for (var c = range.leftInt(); c < range.rightInt(); c++) {
                consumer.accept((char) c, index++);
            }
        }
    }

    default CharOpenHashSet toSet() {
        final var set = new CharOpenHashSet();
        forEachChar((c, i) -> set.add(c));
        return set;
    }
}
