/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.sync;

import io.karma.pda.api.common.session.Session;
import io.karma.pda.api.common.sync.Sync;
import io.karma.pda.api.common.sync.Synced;
import io.karma.pda.api.common.sync.Synchronizer;
import io.karma.pda.api.common.util.Exceptions;
import io.karma.pda.common.PDAMod;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * @author Alexander Hinze
 * @since 24/04/2024
 */
public class DefaultSynchronizer implements Synchronizer {
    protected static final ConcurrentHashMap<Class<?>, List<Pair<Field, VarHandle>>> FIELD_HANDLE_CACHE = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<UUID, Synced<?>> fields = new ConcurrentHashMap<>();
    protected final Session session;

    public DefaultSynchronizer(final Session session) {
        this.session = session;
    }

    private static boolean isSyncedField(final Field field) {
        return Synced.class.isAssignableFrom(field.getType()) && field.isAnnotationPresent(Sync.class);
    }

    private static Pair<Sync, Synced<?>> getFieldPair(final Object instance, final Pair<Field, VarHandle> pair) {
        final var field = pair.getLeft();
        try {
            PDAMod.LOGGER.debug("Reflecting synced property '{}' in {}",
                field.getName(),
                field.getDeclaringClass().getName());
            return Pair.of(field.getAnnotation(Sync.class), (Synced<?>) pair.getRight().get(instance));
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error("Could not retrieve field pair for '{}' in {}: {}",
                field.getName(),
                field.getDeclaringClass().getName(),
                Exceptions.toFancyString(error));
            return null;
        }
    }

    private static List<Pair<Field, VarHandle>> findFields(final Object instance) {
        return FIELD_HANDLE_CACHE.computeIfAbsent(instance.getClass(), type -> {
            try {
                // @formatter:off
                return Arrays.stream(type.getDeclaredFields())
                    .filter(DefaultSynchronizer::isSyncedField)
                    .map(field -> {
                        try {
                            field.setAccessible(true);
                            return Pair.of(field, MethodHandles.lookup().unreflectVarHandle(field));
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
                PDAMod.LOGGER.error("Could not reflect fields in {}: {}",
                    type.getName(),
                    Exceptions.toFancyString(error));
                return Collections.emptyList();
            }
        });
    }

    private static List<Pair<Sync, Synced<?>>> getFields(final Object instance) {
        // @formatter:off
        return findFields(instance).stream()
            .map(p -> getFieldPair(instance, p))
            .toList();
        // @formatter:on
    }

    @Override
    public void register(final Synced<?> value) {
        fields.put(value.getId(), value);
        PDAMod.LOGGER.debug("Registered synced property {}", value.getId());
    }

    @Override
    public void register(final Object instance) {
        try { // @formatter:off
            getFields(instance).stream()
                .map(pair -> {
                    final var value = pair.getRight();
                    value.setPersistent(pair.getLeft().value());
                    return value;
                })
                .forEach(this::register);
        } // @formatter:on
        catch (Throwable error) {
            PDAMod.LOGGER.error("Could not register object {} to synchronizer: {}",
                instance,
                Exceptions.toFancyString(error));
        }
    }

    @Override
    public void unregister(final Synced<?> value) {
        final var id = value.getId();
        fields.remove(id);
        PDAMod.LOGGER.debug("Unregistered synced property {}", id);
    }

    @Override
    public void unregister(final Object instance) {
        try { // @formatter:off
            getFields(instance).stream()
                .map(Pair::getRight)
                .forEach(this::unregister);
        } // @formatter:on
        catch (Throwable error) {
            PDAMod.LOGGER.error("Could not unregister object {} from synchronizer: {}",
                instance,
                Exceptions.toFancyString(error));
        }
    }

    @Override
    public CompletableFuture<Void> flush(final Predicate<Synced<?>> filter) {
        return CompletableFuture.completedFuture(null);
    }
}
