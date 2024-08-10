/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.state;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Alexander Hinze
 * @since 07/06/2024
 */
final class DerivedState<T, R> implements State<R> {
    private final Class<R> type;
    private final State<T> source;
    private final AtomicReference<R> value = new AtomicReference<>(null);
    private final AtomicReference<BiConsumer<State<R>, R>> changeCallback = new AtomicReference<>((state, value) -> {
    });
    private final AtomicReference<String> stateKey = new AtomicReference<>();

    DerivedState(final State<T> source, final Function<T, R> function, final Class<R> type) {
        this.type = type;
        this.source = source;
        source.onChanged((state, value) -> this.value.set(function.apply(value)));
    }

    @Override
    public @Nullable String getStateKey() {
        return stateKey.get();
    }

    @Override
    public void setStateKey(String key) {
        stateKey.set(key);
    }

    @Override
    public BiConsumer<State<R>, R> getChangeCallback() {
        return changeCallback.get();
    }

    @Override
    public String getName() {
        return source.getName();
    }

    @Override
    public boolean isPersistent() {
        return false; // Derived states cannot be persistent
    }

    @Override
    public void onChanged(final BiConsumer<State<R>, R> callback) {
        changeCallback.set(changeCallback.get().andThen(callback));
    }

    @Override
    public Class<R> getType() {
        return type;
    }

    @Override
    public R get() {
        return value.get();
    }
}
