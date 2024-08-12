/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.state;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.karma.pda.api.session.Session;
import io.karma.pda.api.state.MutableState;
import io.karma.pda.api.state.State;
import io.karma.pda.api.state.StateHandler;
import io.karma.pda.api.state.StateReflector;
import io.karma.pda.api.util.Identifiable;
import io.karma.pda.api.util.LogMarkers;
import io.karma.pda.mod.PDAMod;
import io.karma.pda.mod.network.sb.SPacketSyncValues;
import io.karma.pda.mod.state.DefaultStateReflector;
import io.karma.pda.mod.state.Reflectors;
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
public final class ClientStateHandler implements StateHandler {
    private final Multimap<String, String> queue = Multimaps.newMultimap(new ConcurrentHashMap<>(),
        ConcurrentLinkedDeque::new);
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, MutableState<?>>> fields = new ConcurrentHashMap<>();
    private final Session session;

    public ClientStateHandler(final Session session) {
        super();
        this.session = session;
    }

    private StateReflector getReflector(final Class<?> type) {
        final var annotations = type.getAnnotations();
        for (final var annotation : annotations) {
            final var reflector = Reflectors.get().get(annotation.annotationType());
            if (reflector == null) {
                continue;
            }
            return reflector;
        }
        return DefaultStateReflector.INSTANCE;
    }

    private ConcurrentHashMap<String, MutableState<?>> getOrCreateProps(final String ownerId) {
        return fields.computeIfAbsent(ownerId, id -> new ConcurrentHashMap<>());
    }

    private void addProperty(final String ownerId, final MutableState<?> value) {
        value.onChanged((prop, newValue) -> {
            if (prop.get().equals(newValue)) {
                return;
            }
            queue.put(ownerId, prop.getName());
        });
        register(ownerId, value);
    }

    private void removeProperty(final String ownerId, final MutableState<?> value) {
        queue.remove(ownerId, value.getName());
        unregister(ownerId, value);
    }

    @Override
    public void register(final String owner, final Object instance) {
        final var type = instance.getClass();
        for (final var state : getReflector(type).getStates(type, instance, this::getReflector)) {
            addProperty(owner, state);
        }
    }

    @Override
    public void register(final Identifiable instance) {
        final var type = instance.getClass();
        for (final var state : getReflector(type).getStates(type, instance, this::getReflector)) {
            addProperty(instance.getId().toString(), state);
        }
    }

    @Override
    public void unregister(final String owner, final Object instance) {
        final var type = instance.getClass();
        for (final var state : getReflector(type).getStates(type, instance, this::getReflector)) {
            removeProperty(owner, state);
        }
    }

    @Override
    public void unregister(final Identifiable instance) {
        final var type = instance.getClass();
        for (final var state : getReflector(type).getStates(type, instance, this::getReflector)) {
            removeProperty(instance.getId().toString(), state);
        }
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
            return (Void) null;
        }, PDAMod.EXECUTOR_SERVICE).exceptionally(error -> {
            PDAMod.LOGGER.error(LogMarkers.PROTOCOL, "Could not flush UI state", error);
            return null;
        });
    }
}
