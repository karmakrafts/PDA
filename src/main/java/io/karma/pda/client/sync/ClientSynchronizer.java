/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.sync;

import com.fasterxml.jackson.databind.JsonNode;
import io.karma.pda.api.common.sync.Synced;
import io.karma.pda.api.common.sync.Synchronizer;
import io.karma.pda.api.common.util.Identifiable;
import io.karma.pda.api.common.util.JSONUtils;
import io.karma.pda.api.common.util.LogMarkers;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.network.sb.SPacketSyncValues;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.invoke.VarHandle;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
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
    private final ConcurrentLinkedDeque<Pair<UUID, Synced<?>>> queue = new ConcurrentLinkedDeque<>();

    public ClientSynchronizer(final UUID sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public void flush(final Predicate<Synced<?>> filter) {
        PDAMod.LOGGER.debug(LogMarkers.PROTOCOL, "Submitting synchronizer flush");
        // Offload the sync serialization to our executor service to utilize full CPU
        PDAMod.EXECUTOR_SERVICE.submit(() -> {
            final var values = new HashMap<UUID, JsonNode>();
            while (!queue.isEmpty()) {
                final var pair = queue.removeFirst();
                final var value = pair.getRight();
                if (!filter.test(value)) {
                    continue; // Discard any values which don't match our predicate
                }
                values.put(pair.getLeft(), JSONUtils.MAPPER.valueToTree(value.get()));
            }
            Minecraft.getInstance().execute(() -> PDAMod.CHANNEL.sendToServer(new SPacketSyncValues(sessionId,
                values)));
            PDAMod.LOGGER.debug(LogMarkers.PROTOCOL, "Flushed synchronizer data to server");
        });
    }

    @Override
    public void register(final Synced<?> value) {
        value.setCallback((prop, newValue) -> {

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
