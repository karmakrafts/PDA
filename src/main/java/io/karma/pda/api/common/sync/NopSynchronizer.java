/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.sync;

import io.karma.pda.api.common.util.Identifiable;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public final class NopSynchronizer implements Synchronizer {
    public static final NopSynchronizer INSTANCE = new NopSynchronizer();

    // @formatter:off
    private NopSynchronizer() {}
    // @formatter:on

    @Override
    public void register(final UUID id, final Synced<?> value) {
    }

    @Override
    public void register(final Identifiable object) {

    }

    @Override
    public void unregister(final UUID id) {
    }

    @Override
    public void flush() {
    }
}
