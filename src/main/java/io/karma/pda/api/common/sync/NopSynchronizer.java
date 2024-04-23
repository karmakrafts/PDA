/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.sync;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

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
    public void register(final Synced<?> value, final boolean isPersistent) {

    }

    @Override
    public void register(final Object instance) {

    }

    @Override
    public void unregister(final Synced<?> value) {

    }

    @Override
    public void unregister(final Object instance) {

    }

    @Override
    public CompletableFuture<Void> flush(final Predicate<Synced<?>> filter) {
        return CompletableFuture.completedFuture(null);
    }
}
