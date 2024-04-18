/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.sync;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
final class DefaultSynced<T> implements Synced<T> {
    private final Class<T> type;
    private final AtomicReference<UUID> id = new AtomicReference<>();
    private final AtomicReference<T> value = new AtomicReference<>();
    private final AtomicReference<SyncCodec<T>> codec = new AtomicReference<>();
    private final AtomicReference<BiConsumer<Synced<T>, T>> callback = new AtomicReference<>();

    DefaultSynced(final UUID id, final Class<T> type, final @Nullable T initial) {
        this.id.set(id);
        this.type = type;
        value.set(initial);
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
    public UUID getId() {
        return id.get();
    }

    @Override
    public void setId(final UUID id) {
        this.id.set(id);
    }

    @Override
    public void setCallback(final @Nullable BiConsumer<Synced<T>, T> callback) {
        this.callback.set(callback);
    }

    @Override
    public @Nullable BiConsumer<Synced<T>, T> getCallback() {
        return callback.get();
    }

    @Override
    public T get() {
        return value.get();
    }

    @Override
    public SyncCodec<T> getCodec() {
        return codec.get();
    }

    @Override
    public void setCodec(final SyncCodec<T> codec) {
        this.codec.set(codec);
    }
}
