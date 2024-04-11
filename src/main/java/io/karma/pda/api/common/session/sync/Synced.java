/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.session.sync;

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

    void onChanged(final BiConsumer<T, T> callback);

    static <T> Synced<T> ofType(final Class<T> type) {
        return new Synced<T>() {
            private T value;
            private BiConsumer<T, T> callback = (o, n) -> {
            };

            @Override
            public @Nullable T get() {
                return value;
            }

            @Override
            public void set(final @Nullable T value) {
                callback.accept(this.value, value);
                this.value = value;
            }

            @Override
            public void onChanged(final BiConsumer<T, T> callback) {
                this.callback = this.callback.andThen(callback);
            }

            @Override
            public Class<T> getType() {
                return type;
            }
        };
    }

    @SuppressWarnings("unchecked")
    static <T> Synced<T> withInitial(final T initial) {
        return new Synced<T>() {
            private final Class<T> type = (Class<T>) initial.getClass();
            private T value = initial;
            private BiConsumer<T, T> callback = (o, n) -> {
            };

            @Override
            public @Nullable T get() {
                return value;
            }

            @Override
            public void set(final @Nullable T value) {
                callback.accept(this.value, value);
                this.value = value;
            }

            @Override
            public void onChanged(final BiConsumer<T, T> callback) {
                this.callback = this.callback.andThen(callback);
            }

            @Override
            public Class<T> getType() {
                return type;
            }
        };
    }
}
