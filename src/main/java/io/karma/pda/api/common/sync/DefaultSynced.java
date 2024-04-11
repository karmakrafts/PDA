/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.sync;

import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
final class DefaultSynced<T> implements Synced<T> {
    private final Class<T> type;
    private T value;
    private BiConsumer<T, T> callback = (previous, current) -> {
    };

    @SuppressWarnings("unchecked")
    DefaultSynced(final T initial) {
        this.type = (Class<T>) initial.getClass();
        this.value = initial;
    }

    DefaultSynced(final Class<T> type) {
        this.type = type;
    }

    @Override
    public void set(final @Nullable T value) {
        callback.accept(this.value, value);
        this.value = value;
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public void onChanged(final BiConsumer<T, T> callback) {
        this.callback = callback.andThen(callback);
    }

    @Override
    public T get() {
        return value;
    }
}
