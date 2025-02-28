/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.session;

import io.karma.pda.api.app.Launcher;
import io.karma.pda.api.state.StateHandler;
import io.karma.pda.api.util.Identifiable;

import java.time.Instant;

/**
 * A session describes a temporary pipe between client and server
 * that is used to exchange mutable display data in realtime to reduce
 * overhead introduced by NBT synchronization.
 *
 * @author Alexander Hinze
 * @since 04/04/2024
 */
public interface Session extends Identifiable {
    /**
     * The launcher used by the current session.
     * This is the main interface for starting, closing and managing active apps.
     *
     * @return The launcher instance associated with the current session.
     */
    Launcher getLauncher();

    /**
     * Retrieves the context in which the session was created.
     * This includes the player entity who created the session,
     * the level said player is currently in and the position
     * of the player or the dock the player is using.
     *
     * @return An instance of {@link SessionContext} associated
     * with this session on its creation.
     */
    SessionContext getContext();

    /**
     * @return The state handler instance associated with this
     * session by its UUID.
     */
    StateHandler getStateHandler();

    /**
     * Retrieves the instant in which this session was created.
     *
     * @return The instant in which this session was created.
     */
    Instant getCreationTime();

    /**
     * Called when the session is fully established by the underlying session handler.
     */
    default void onEstablished() {
    }

    /**
     * Called when the session is terminated by the underlying session handler.
     */
    default void onTerminated() {
    }
}
