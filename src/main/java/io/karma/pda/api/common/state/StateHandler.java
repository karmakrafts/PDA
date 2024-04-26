/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.state;

import io.karma.pda.api.common.util.Identifiable;

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
public interface StateHandler {
    // TODO: document
    void register(final String owner, final Object instance);

    /**
     * Register the given object instance to this state handler
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
    void register(final Identifiable instance);

    // TODO: document
    void unregister(final String owner, final Object instance);

    /**
     * Unregister the given object instance from this
     * synchronized and stop recording every change of every
     * Synced field within the instance to be sent to the server.
     *
     * @param instance The instance to unregister.
     */
    void unregister(final Identifiable instance);

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
    CompletableFuture<Void> flush(final Predicate<State<?>> filter);

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
