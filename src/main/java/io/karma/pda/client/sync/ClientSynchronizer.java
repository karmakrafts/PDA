/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.sync;

import com.fasterxml.jackson.databind.JsonNode;
import io.karma.pda.api.common.sync.Synced;
import io.karma.pda.api.common.sync.Synchronizer;
import io.karma.pda.api.common.util.JSONUtils;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.network.sb.SPacketSyncValues;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author Alexander Hinze<
 * @since 11/04/2024
 */
public final class ClientSynchronizer implements Synchronizer {
    private final UUID id;
    private final ArrayDeque<Pair<UUID, Synced<?>>> queue = new ArrayDeque<>();

    public ClientSynchronizer(final UUID id) {
        this.id = id;
    }

    @Override
    public void flush() {
        final var values = new HashMap<UUID, JsonNode>();
        while (!queue.isEmpty()) {
            final var pair = queue.removeFirst();
            values.put(pair.getLeft(), JSONUtils.MAPPER.valueToTree(pair.getRight().get()));
        }
        Minecraft.getInstance().execute(() -> PDAMod.CHANNEL.sendToServer(new SPacketSyncValues(id, values)));
    }

    @SuppressWarnings("all")
    @Override
    public void register(final UUID id, final Synced<?> value) {
        value.onChanged((previous, current) -> {
            if (current.equals(previous) || queue.contains(value)) {
                return;
            }
            queue.addLast(Pair.of(id, value));
        });
    }
}
