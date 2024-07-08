/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.util;

/**
 * @author Alexander Hinze
 * @since 15/06/2024
 */
@FunctionalInterface
public interface ToBooleanBiFunction<T, U> {
    boolean apply(final T t, final U u);
}
