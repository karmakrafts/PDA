/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.sync;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public final class NoopSynchronizer implements Synchronizer {
    public static final NoopSynchronizer INSTANCE = new NoopSynchronizer();

    // @formatter:off
    private NoopSynchronizer() {}
    // @formatter:on

    @Override
    public void register(final UUID id, final Synced<?> value) {
    }

    @Override
    public void flush() {
    }
}
