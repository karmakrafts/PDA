/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.sync;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * An instance of this interface may behave like a regular property;
 * its value can be set and get. But it also provides a unique ID, a
 * way to override said unique ID when needed, an accessor for the underlying
 * value type and a callback mechanism.
 * <p>
 * The callback mechanism allows instances of {@link Synchronizer} to
 * inject itself into existing and most-importantly final fields which
 * are to be synchronized from client to server, and from server to
 * all remaining clients.
 *
 * @author Alexander Hinze
 * @since 11/04/2024
 */
public interface Synced<T> extends Supplier<T> {
    /**
     * Creates a new synchronized property instance with
     * an initial value of null and the given type.
     * The ID for this property will be generated randomly
     * and synchronized automatically.
     *
     * @param type An instance of the underlying value's type.
     * @param <T>  The type of the newly created property.
     * @return A new synchronized property instance with the given type.
     */
    static <T> Synced<T> create(final Class<T> type) {
        return new DefaultSynced<>(UUID.randomUUID(), type);
    }

    /**
     * Creates a new synchronized property instance with
     * the given initial value. The ID for this property
     * will be generated randomly and synchronized automatically.
     * The type of the property is derived from the given value.
     *
     * @param value The value to set the property to initially.
     * @param <T>   The type of the newly created property.
     * @return A new synchronized property instance with the given initial value.
     */
    static <T> Synced<T> withInitial(final T value) {
        return new DefaultSynced<>(UUID.randomUUID(), value);
    }

    /**
     * Sets the value contained within this synchronized property.
     *
     * @param value The value to set this property to.
     */
    void set(final @Nullable T value);

    /**
     * Retrieves the type of the value contained
     * within this property, regardless whether it's null or not.
     *
     * @return The type of the value contained within this property.
     */
    Class<T> getType();

    /**
     * Retrieves the unique ID of this property.
     *
     * @return The unique ID of this property.
     */
    UUID getId();

    /**
     * Retrieves the callback invoked by this property
     * upon being set if present.
     *
     * @return The callback invoke by this property upon being set if present.
     */
    @Nullable
    BiConsumer<Synced<T>, T> getCallback();

    /**
     * Retrieves the codec instance used to en- and decode
     * data by this property in the given {@link Synchronizer}.
     *
     * @return The codec instance used to en- and decode data by this property.
     */
    SyncCodec<T> getCodec();

    /**
     * Sets the codec instance used to en- and decode data
     * by this property in the given {@link Synchronizer}.
     *
     * @param codec The codec instance used to en- and decode data
     *              by this property.
     */
    @ApiStatus.Internal
    void setCodec(final SyncCodec<T> codec);

    /**
     * Sets the callback invoked by this property upon
     * being set.
     *
     * @param callback The callback invoked by this property upon being set.
     */
    @ApiStatus.Internal
    void setCallback(final @Nullable BiConsumer<Synced<T>, T> callback);

    /**
     * Sets the unique ID of this property to the given ID.
     * Should not be changed manually as it is synchronized
     * between client and server.
     *
     * @param id The new unique ID of this property.
     */
    @ApiStatus.Internal
    void setId(final UUID id);
}
