/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.sync;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
final class DefaultSynced<T> implements Synced<T> {
    @JsonIgnore
    private final Class<T> type;
    @JsonIgnore
    private T value;
    @JsonIgnore
    private BiConsumer<T, T> callback;

    @JsonCreator
    @SuppressWarnings("unchecked")
    public DefaultSynced(final T initial) {
        this.type = (Class<T>) initial.getClass();
        this.value = initial;
    }

    @JsonIgnore
    DefaultSynced(final Class<T> type) {
        this.type = type;
    }

    @JsonIgnore
    @Override
    public void set(final @Nullable T value) {
        if (callback != null) {
            callback.accept(this.value, value);
        }
        this.value = value;
    }

    @JsonIgnore
    @Override
    public Class<T> getType() {
        return type;
    }

    @JsonIgnore
    @Override
    public void setCallback(final @Nullable BiConsumer<T, T> callback) {
        this.callback = callback;
    }

    @JsonIgnore
    @Override
    public @Nullable BiConsumer<T, T> getCallback() {
        return callback;
    }

    @JsonAnyGetter
    @Override
    public T get() {
        return value;
    }
}
