/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.state;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
final class DefaultState<T> implements MutableState<T> {
    private final AtomicReference<Supplier<T>> value = new AtomicReference<>();
    private final Class<T> type;
    private final AtomicReference<BiConsumer<State<T>, T>> changeCallback = new AtomicReference<>((state, value) -> {
    });
    private final AtomicReference<String> name = new AtomicReference<>();
    private final AtomicBoolean isPersistent = new AtomicBoolean(true);
    private final AtomicReference<String> stateKey = new AtomicReference<>();

    DefaultState(final Class<T> type, final @Nullable T initial) {
        this.type = type;
        value.set(() -> initial);
    }

    @Override
    public @Nullable String getStateKey() {
        return stateKey.get();
    }

    @Override
    public void setStateKey(final String key) {
        stateKey.set(key);
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public void onChanged(final BiConsumer<State<T>, T> callback) {
        changeCallback.set(changeCallback.get().andThen(callback));
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
    public void setName(final String name) {
        this.name.set(name);
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
    public BiConsumer<State<T>, T> getChangeCallback() {
        return changeCallback.get();
    }

    @Override
    public void set(final Supplier<@Nullable T> supplier) {
        this.value.set(supplier);
    }

    @Override
    public T get() {
        return value.get().get();
    }
}
