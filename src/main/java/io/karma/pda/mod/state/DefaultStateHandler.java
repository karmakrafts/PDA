/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.state;

import io.karma.pda.api.session.Session;
import io.karma.pda.api.state.MutableState;
import io.karma.pda.api.state.State;
import io.karma.pda.api.state.StateHandler;
import io.karma.pda.api.state.StateReflector;
import io.karma.pda.api.util.Identifiable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * @author Alexander Hinze
 * @since 24/04/2024
 */
public final class DefaultStateHandler implements StateHandler {
    private final Session session;
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, MutableState<?>>> fields = new ConcurrentHashMap<>();

    public DefaultStateHandler(final Session session) {
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

    private void addProperty(final String ownerId, final MutableState<?> property) {
        final var properties = getOrCreateProps(ownerId);
        if (properties.containsValue(property)) {
            throw new IllegalStateException("Property already registered");
        }
        properties.put(property.getName(), property);
    }

    private void removeProperty(final String ownerId, final MutableState<?> property) {
        final var properties = fields.get(ownerId);
        if (properties == null || properties.remove(property.getName()) == null) {
            throw new IllegalArgumentException("No such property");
        }
        if (properties.isEmpty()) {
            fields.remove(ownerId); // Free up space we don't need for this owner
        }
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
        return CompletableFuture.completedFuture(null);
    }
}
