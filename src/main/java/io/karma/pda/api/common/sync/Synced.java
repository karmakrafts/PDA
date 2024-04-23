/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.sync;

import io.karma.pda.api.common.util.TypedValue;
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
public interface Synced<T> extends TypedValue<T> {
    /**
     * Creates a new synchronized property instance with an initial value of null and the given type.
     * The ID for this property will be generated randomly and synchronized automatically.
     *
     * @param type An instance of the underlying value's type.
     * @param <T>  The type of the newly created property.
     * @return A new synchronized property instance with the given type.
     */
    static <T> Synced<T> ofNull(final Class<T> type) {
        return new DefaultSynced<>(UUID.randomUUID(), type, null);
    }

    /**
     * Creates a new synchronized property instance with the given initial value.
     * The ID for this property will be generated randomly and synchronized automatically.
     * The type of the property is derived from the given value.
     *
     * @param value The value to set the property to initially.
     * @param <T>   The type of the newly created property.
     * @return A new synchronized property instance with the given initial value.
     */
    @SuppressWarnings("unchecked")
    static <T> Synced<T> of(final T value) {
        return new DefaultSynced<>(UUID.randomUUID(), (Class<T>) value.getClass(), value);
    }

    /**
     * Creates a new synchronized property instance whose instance
     * is provided by the given supplier function. The property created
     * by this function is immutable and cannot be set directly.
     * A call to {@link Synced#set(Object)} will throw an {@link UnsupportedOperationException}.
     *
     * @param type     The type of the property to create.
     * @param delegate The function which to call when retrieving the value of the property.
     * @param <T>      The type of the newly created property.
     * @return A new synchronized property instance with the given delegate function.
     */
    static <T> Synced<T> by(final Class<T> type, final Supplier<T> delegate) {
        return new DelegateSynced<>(UUID.randomUUID(), type, delegate);
    }

    /**
     * Sets the value contained within this synchronized property.
     *
     * @param value The value to set this property to.
     */
    void set(final @Nullable T value);

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
