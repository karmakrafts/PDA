/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.util;

/**
 * @author Alexander Hinze
 * @since 26/08/2024
 */
@FunctionalInterface
public interface IntBiConsumer {
    void accept(int left, int right);

    default IntBiConsumer andThen(final IntBiConsumer other) {
        return (left, right) -> {
            accept(left, right);
            other.accept(left, right);
        };
    }
}
