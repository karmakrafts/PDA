/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.state;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
final class DefaultState<T> implements MutableState<T> {
    private final Class<T> type;
    private final AtomicReference<T> value = new AtomicReference<>();
    private final AtomicReference<BiConsumer<State<T>, T>> callback = new AtomicReference<>();
    private final AtomicReference<String> name = new AtomicReference<>();
    private final AtomicBoolean isPersistent = new AtomicBoolean(true);

    DefaultState(final Class<T> type, final @Nullable T initial) {
        this.type = type;
        value.set(initial);
    }

    @Override
    public void setName(final String name) {
        this.name.set(name);
    }

    @Override
    public String getName() {
        final var name = this.name.get();
        if (name == null) {
            throw new IllegalStateException("Name not set");
        }
        return name;
    }

    @Override
    public boolean isPersistent() {
        return isPersistent.get();
    }

    @Override
    public void setPersistent(final boolean isPersistent) {
        this.isPersistent.set(isPersistent);
    }

    @Override
    public void set(final @Nullable T value) {
        final var callback = this.callback.get();
        if (callback != null) {
            callback.accept(this, value);
        }
        this.value.set(value);
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public void setCallback(final @Nullable BiConsumer<State<T>, T> callback) {
        this.callback.set(callback);
    }

    @Override
    public @Nullable BiConsumer<State<T>, T> getCallback() {
        return callback.get();
    }

    @Override
    public T get() {
        return value.get();
    }
}
