/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.session;

import io.karma.pda.api.common.app.Launcher;
import io.karma.pda.api.common.sync.NopSynchronizer;
import io.karma.pda.api.common.sync.Synchronizer;
import io.karma.pda.api.common.util.Identifiable;

import java.util.function.Supplier;

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
     * Retrieves the synchronizer instance associated with this
     * session by its UUID. This is used for UI synchronization
     * through functions like {@link io.karma.pda.api.common.app.component.Component#doWithSynchronizer(Supplier)}.
     *
     * @return The synchronizer instance associated with this
     * session by its UUID.
     */
    default Synchronizer getSynchronizer() {
        return NopSynchronizer.INSTANCE;
    }
}
