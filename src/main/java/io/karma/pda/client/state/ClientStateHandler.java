/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.state;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.karma.pda.api.common.session.Session;
import io.karma.pda.api.common.state.MutableState;
import io.karma.pda.api.common.state.State;
import io.karma.pda.api.common.util.LogMarkers;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.network.sb.SPacketSyncValues;
import io.karma.pda.common.state.DefaultStateHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Alexander Hinze<
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ClientStateHandler extends DefaultStateHandler {
    private final Multimap<String, String> queue = Multimaps.newMultimap(new ConcurrentHashMap<>(),
        ConcurrentLinkedDeque::new);

    public ClientStateHandler(final Session session) {
        super(session);
    }

    @Override
    protected void addProperty(final String ownerId, final MutableState<?> value) {
        value.setUpdateCallback((prop, newValue) -> {
            if (prop.get().equals(newValue)) {
                return;
            }
            queue.put(ownerId, prop.getName());
        });
        super.register(ownerId, value);
    }

    @Override
    protected void removeProperty(final String ownerId, final MutableState<?> value) {
        value.setUpdateCallback(null);
        queue.remove(ownerId, value.getName());
        super.unregister(ownerId, value);
    }

    @Override
    public CompletableFuture<Void> flush(final Predicate<State<?>> filter) {
        if (queue.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.supplyAsync(() -> {
            // @formatter:off
            final var values = queue.asMap()
                .entrySet()
                .stream()
                .map(e -> Pair.of(e.getKey(), getOrCreateProps(e.getKey())
                    .values()
                    .stream()
                    .filter(s -> e.getValue().contains(s.getName()))
                    .collect(Collectors.toMap(State::getName, s -> s))))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
            // @formatter:on
            PDAMod.LOGGER.debug(LogMarkers.PROTOCOL, "Synchronizing {} changed values", values.size());
            Minecraft.getInstance().execute(() -> PDAMod.CHANNEL.sendToServer(new SPacketSyncValues(session.getId(),
                values)));
            return null;
        }, PDAMod.EXECUTOR_SERVICE);
    }
}
