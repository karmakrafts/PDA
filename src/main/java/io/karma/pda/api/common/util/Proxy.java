/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.util;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 30/04/2024
 */
@FunctionalInterface
public interface Proxy<T> extends Supplier<T> {
    default <R> Proxy<R> map(final Function<T, R> mapper) {
        return () -> mapper.apply(get());
    }
}
