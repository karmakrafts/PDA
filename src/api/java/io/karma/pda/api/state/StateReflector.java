/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.state;

import java.util.List;
import java.util.function.Function;

/**
 * @author Alexander Hinze
 * @since 26/04/2024
 */
public interface StateReflector {
    default void init() {
    }

    List<? extends MutableState<?>> getStates(final Class<?> type, final Object instance,
                                              final Function<Class<?>, StateReflector> reflectorGetter);
}
