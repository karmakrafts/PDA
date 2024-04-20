/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.util;

import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 18/04/2024
 */
public interface TypedValue<T> extends Supplier<T> {
    static <T> TypedValue<T> fromPair(final Pair<Class<T>, T> pair) {
        return new TypedValue<>() {
            @Override
            public Class<T> getType() {
                return pair.getLeft();
            }

            @Override
            public T get() {
                return pair.getRight();
            }
        };
    }

    static <T> TypedValue<T> of(final T value) {
        return new TypedValue<>() {
            @SuppressWarnings("unchecked")
            final Class<T> type = (Class<T>) value.getClass();

            @Override
            public Class<T> getType() {
                return type;
            }

            @Override
            public T get() {
                return value;
            }
        };
    }

    static <T> TypedValue<T> ofNull(final Class<T> type) {
        return new TypedValue<>() {
            @Override
            public Class<T> getType() {
                return type;
            }

            @Override
            public T get() {
                return null;
            }
        };
    }

    /**
     * Retrieves the type of the value contained
     * within this property, regardless whether it's null or not.
     *
     * @return The type of the value contained within this property.
     */
    Class<T> getType();
}
