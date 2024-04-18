/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.sync;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
final class DefaultSynced<T> implements Synced<T> {
    private final Class<T> type;
    private UUID id;
    private T value;
    private SyncCodec<T> codec;
    private BiConsumer<Synced<T>, T> callback;

    @SuppressWarnings("unchecked")
    public DefaultSynced(final UUID id, final T initial) {
        this.id = id;
        this.type = (Class<T>) initial.getClass();
        this.value = initial;
    }

    DefaultSynced(final UUID id, final Class<T> type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public void set(final @Nullable T value) {
        if (callback != null) {
            callback.accept(this, value);
        }
        this.value = value;
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(final UUID id) {
        this.id = id;
    }

    @Override
    public void setCallback(final @Nullable BiConsumer<Synced<T>, T> callback) {
        this.callback = callback;
    }

    @Override
    public @Nullable BiConsumer<Synced<T>, T> getCallback() {
        return callback;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public SyncCodec<T> getCodec() {
        return codec;
    }

    @Override
    public void setCodec(final SyncCodec<T> codec) {
        this.codec = codec;
    }
}
