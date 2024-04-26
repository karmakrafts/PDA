/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.state;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 23/04/2024
 */
final class DelegateState<T> implements MutableState<T> {
    private final Class<T> type;
    private final AtomicReference<Supplier<@Nullable T>> delegate = new AtomicReference<>();
    private final AtomicReference<BiConsumer<State<T>, T>> callback = new AtomicReference<>();
    private final AtomicReference<T> lastValue = new AtomicReference<>();
    private final AtomicReference<String> name = new AtomicReference<>();
    private final AtomicBoolean isPersistent = new AtomicBoolean(true);

    DelegateState(final Class<T> type, final Supplier<@Nullable T> delegate) {
        this.type = type;
        this.delegate.set(delegate);
        lastValue.set(delegate.get()); // Setup initial value
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
        delegate.set(() -> value);
    }

    @Override
    public @Nullable BiConsumer<State<T>, T> getCallback() {
        return callback.get();
    }

    @Override
    public void setCallback(final @Nullable BiConsumer<State<T>, T> callback) {
        this.callback.set(callback);
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public T get() {
        final var value = delegate.get().get();
        if (value != null && value.equals(lastValue.get())) {
            return value;
        }
        callback.get().accept(this, value);
        lastValue.set(value);
        return value;
    }
}
