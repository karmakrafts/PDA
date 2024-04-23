/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.sync;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 23/04/2024
 */
final class DelegateSynced<T> implements Synced<T> {
    private final AtomicReference<UUID> id = new AtomicReference<>();
    private final Class<T> type;
    private final AtomicReference<Supplier<@Nullable T>> delegate = new AtomicReference<>();
    private final AtomicReference<BiConsumer<Synced<T>, T>> callback = new AtomicReference<>();
    private final AtomicReference<SyncCodec<T>> codec = new AtomicReference<>();
    private final AtomicReference<T> lastValue = new AtomicReference<>();

    DelegateSynced(final UUID id, final Class<T> type, final Supplier<@Nullable T> delegate) {
        this.id.set(id);
        this.type = type;
        this.delegate.set(delegate);
        lastValue.set(delegate.get()); // Setup initial value
    }

    @Override
    public void set(final @Nullable T value) {
        delegate.set(() -> value);
    }

    @Override
    public UUID getId() {
        return id.get();
    }

    @Override
    public @Nullable BiConsumer<Synced<T>, T> getCallback() {
        return callback.get();
    }

    @Override
    public SyncCodec<T> getCodec() {
        return codec.get();
    }

    @Override
    public void setCodec(final SyncCodec<T> codec) {
        this.codec.set(codec);
    }

    @Override
    public void setCallback(final @Nullable BiConsumer<Synced<T>, T> callback) {
        this.callback.set(callback);
    }

    @Override
    public void setId(final UUID id) {
        this.id.set(id);
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
