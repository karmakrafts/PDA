/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.sync;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 18/04/2024
 */
@FunctionalInterface
public interface SyncEncodeFunction<T> {
    void encode(final String name, final Class<T> type, final @Nullable T value, final ObjectNode node);

    @SuppressWarnings("unchecked")
    default void encode(final String name, final T value, final ObjectNode node) {
        encode(name, (Class<T>) value.getClass(), value, node);
    }
}
