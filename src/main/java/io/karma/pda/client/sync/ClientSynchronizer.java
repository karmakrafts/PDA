/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.sync;

import io.karma.pda.api.common.session.Session;
import io.karma.pda.api.common.sync.Synced;
import io.karma.pda.api.common.util.LogMarkers;
import io.karma.pda.api.common.util.StreamUtils;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.network.sb.SPacketSyncValues;
import io.karma.pda.common.sync.DefaultSynchronizer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Alexander Hinze<
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ClientSynchronizer extends DefaultSynchronizer {
    private final ConcurrentLinkedDeque<UUID> queue = new ConcurrentLinkedDeque<>();

    public ClientSynchronizer(final Session session) {
        super(session);
    }

    @Override
    public void register(final Synced<?> value) {
        value.setCallback((prop, newValue) -> {
            if (prop.get().equals(newValue)) {
                return;
            }
            queue.add(value.getId());
        });
        super.register(value);
    }

    @Override
    public void unregister(final Synced<?> value) {
        value.setCallback(null);
        queue.removeIf(value.getId()::equals);
        super.unregister(value);
    }

    @Override
    public CompletableFuture<Void> flush(final Predicate<Synced<?>> filter) {
        if (queue.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.supplyAsync(() -> {
            // @formatter:off
            final var values = StreamUtils.consume(queue)
                .map(fields::get)
                .filter(filter)
                .collect(Collectors.<Synced<?>, UUID, Synced<?>>toMap(Synced::getId, s -> s));
            PDAMod.LOGGER.debug(LogMarkers.PROTOCOL, "Synchronizing {} changed values", values.size());
            Minecraft.getInstance().execute(
                () -> PDAMod.CHANNEL.sendToServer(new SPacketSyncValues(session.getId(), values)));
            // @formatter:on
            return null;
        }, PDAMod.EXECUTOR_SERVICE);
    }
}
