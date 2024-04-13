/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.sync;

import io.karma.pda.api.common.util.Identifiable;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
public interface Synchronizer {
    void register(final UUID id, final Synced<?> value);

    void unregister(final UUID id);

    void flush();

    void register(final Identifiable object);

    default void unregister(final Identifiable object) {
        unregister(object.getId());
    }
}
