/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.sync;

import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
public interface Synced<T> extends Supplier<T> {
    void set(final @Nullable T value);

    Class<T> getType();

    void setCallback(final @Nullable BiConsumer<T, T> callback);

    @Nullable
    BiConsumer<T, T> getCallback();

    static <T> Synced<T> ofType(final Class<T> type) {
        return new DefaultSynced<>(type);
    }

    static <T> Synced<T> withInitial(final T initial) {
        return new DefaultSynced<>(initial);
    }
}
