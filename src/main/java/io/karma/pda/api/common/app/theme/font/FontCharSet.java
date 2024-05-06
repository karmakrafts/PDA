/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.theme.font;

import it.unimi.dsi.fastutil.chars.CharCharPair;
import it.unimi.dsi.fastutil.chars.CharConsumer;
import it.unimi.dsi.fastutil.chars.CharOpenHashSet;

/**
 * @author Alexander Hinze
 * @since 05/05/2024
 */
public interface FontCharSet {
    CharCharPair[] getRanges();

    void forEachChar(final CharConsumer consumer);

    default CharOpenHashSet toSet() {
        final var set = new CharOpenHashSet();
        forEachChar(set::add);
        return set;
    }
}
