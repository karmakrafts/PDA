/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.session.sync;

import io.karma.pda.api.common.session.sync.Synced;
import io.karma.pda.api.common.session.sync.Synchronizer;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
public final class ClientSynchronizer implements Synchronizer {
    @Override
    public void register(final UUID owner, final Synced<?> value) {

    }
}
