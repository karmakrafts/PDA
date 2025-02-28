/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.util;

/**
 * @author Alexander Hinze
 * @since 08/05/2024
 */
@FunctionalInterface
public interface CharIntConsumer {
    void accept(final char c, final int i);

    default CharIntConsumer andThen(final CharIntConsumer other) {
        return (c, i) -> {
            accept(c, i);
            other.accept(c, i);
        };
    }
}
