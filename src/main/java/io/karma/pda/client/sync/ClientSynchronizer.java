/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.sync;

import io.karma.pda.api.common.sync.Sync;
import io.karma.pda.api.common.sync.Synced;
import io.karma.pda.api.common.sync.Synchronizer;
import io.karma.pda.api.common.util.Exceptions;
import io.karma.pda.common.PDAMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.*;
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
    private static final ConcurrentHashMap<Class<?>, List<VarHandle>> FIELD_CACHE = new ConcurrentHashMap<>();
    private final UUID sessionId;
    private final ConcurrentHashMap<UUID, Synced<?>> fields = new ConcurrentHashMap<>();
    private final ConcurrentLinkedDeque<UUID> queue = new ConcurrentLinkedDeque<>();

    public ClientSynchronizer(final UUID sessionId) {
        this.sessionId = sessionId;
    }

    private static List<VarHandle> findFields(final Class<?> type) throws IllegalAccessException {
        return FIELD_CACHE.computeIfAbsent(type, t -> {
            try {
                final var lookup = MethodHandles.privateLookupIn(type, MethodHandles.lookup());
                // @formatter:off
                return Arrays.stream(type.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(Sync.class))
                    .map(field -> {
                        try {
                            return lookup.unreflectVarHandle(field);
                        }
                        catch (Throwable error) {
                            PDAMod.LOGGER.error("Could not unreflect field {} in {}: {}", field.getName(), type,
                                Exceptions.toFancyString(error));
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();
                // @formatter:on
            }
            catch (Throwable error) {
                PDAMod.LOGGER.error("Could not find syncable fields in {}: {}", type, Exceptions.toFancyString(error));
                return Collections.emptyList();
            }
        });
    }

    @Override
    public CompletableFuture<Void> flush(final Predicate<Synced<?>> filter) {
        if (queue.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }
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
            final var id = prop.getId();
            if (queue.contains(id)) {
                return;
            }
            queue.add(id);
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
