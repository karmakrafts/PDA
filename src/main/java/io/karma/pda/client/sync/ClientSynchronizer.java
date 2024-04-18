/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.sync;

import io.karma.pda.api.common.sync.Synced;
import io.karma.pda.api.common.sync.Synchronizer;
import io.karma.pda.api.common.util.Identifiable;
import io.karma.pda.api.common.util.LogMarkers;
import io.karma.pda.common.PDAMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.invoke.VarHandle;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Predicate;

/**
 * @author Alexander Hinze<
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ClientSynchronizer implements Synchronizer {
    private static final ConcurrentHashMap<Class<? extends Identifiable>, List<VarHandle>> FIELD_CACHE = new ConcurrentHashMap<>();
    private final UUID sessionId;
    private final ConcurrentHashMap<UUID, Synced<?>> fields = new ConcurrentHashMap<>();
    private final ConcurrentLinkedDeque<UUID> queue = new ConcurrentLinkedDeque<>();

    public ClientSynchronizer(final UUID sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public CompletableFuture<Void> flush(final Predicate<Synced<?>> filter) {
        PDAMod.LOGGER.debug(LogMarkers.PROTOCOL, "Submitting synchronizer flush");
        // Offload the sync serialization to our executor service to utilize full CPU
        return CompletableFuture.supplyAsync(() -> {

            return null;
        }, PDAMod.EXECUTOR_SERVICE);
    }

    @Override
    public void register(final Synced<?> value) {
        value.setCallback((prop, newValue) -> {
            final var oldValue = prop.get();
            if (oldValue.equals(newValue)) {
                return;
            }
            queue.add(prop.getId());
        });
    }

    @Override
    public void register(final Object instance) {

    }

    @Override
    public void unregister(final Synced<?> value) {
        value.setCallback(null);
    }

    @Override
    public void unregister(final Object instance) {

    }
}
