/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.sync;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

/**
 * This interface provides client-to-server
 * communication and persistence within a session, as well as
 * server-to-client broadcasting to all other
 * clients in order to display live content on PDA displays.
 *
 * @author Alexander Hinze
 * @since 11/04/2024
 */
public interface Synchronizer {
    /**
     * Register the given value to this synchronizer
     * instance and record every value change to be
     * sent to the server.
     *
     * @param value The property to be registered.
     */
    void register(final Synced<?> value);

    /**
     * Register the given object instance to this synchronizer
     * instance and record every value change of every
     * Synced field within the instance to be sent to the server.
     * <p>
     * The field must look like this:
     * {@code @Sync private final Synced<String> value = Synced.create(String.class);}
     * The access modifier of the field does not matter,
     * however it is not possible to make a synchronized field static.
     *
     * @param instance The instance to register.
     */
    void register(final Object instance);

    /**
     * Unregister the given value from this synchronizer
     * instance and stop recording every change of the given property.
     *
     * @param value The property to be unregistered.
     */
    void unregister(final Synced<?> value);

    /**
     * Unregister the given object instance from this
     * synchronized and stop recording every change of every
     * Synced field within the instance to be sent to the server.
     *
     * @param instance The instance to unregister.
     */
    void unregister(final Object instance);

    /**
     * Dequeues all accumulated values in need of synchronization
     * and compiles a compressed update packet to be sent to the server
     * in one go. This will broadcast all changes to the server and to
     * all other clients connected.
     *
     * @param filter A filter function which decides which updates to discard
     *               or retain before compiling the packet data.
     * @return A future which completes after the updates were broadcast.
     */
    CompletableFuture<Void> flush(final Predicate<Synced<?>> filter);

    /**
     * Same as {@link #flush(Predicate)} except
     * that it flushes all properties.
     *
     * @return A future which completes after the updates were broadcast.
     */
    default CompletableFuture<Void> flush() {
        return flush(value -> true);
    }
}
