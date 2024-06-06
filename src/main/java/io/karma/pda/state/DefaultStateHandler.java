/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.state;

import io.karma.pda.PDAMod;
import io.karma.pda.api.session.Session;
import io.karma.pda.api.state.*;
import io.karma.pda.api.util.Identifiable;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Alexander Hinze
 * @since 24/04/2024
 */
public class DefaultStateHandler implements StateHandler {
    // @formatter:off
    protected static final Map<Class<? extends Annotation>, StateReflector> REFLECTORS = PDAMod.STATE_REFLECTORS.stream()
        .map(p -> {
            final var reflector = p.get();
            final var reflectorType = reflector.getClass();
            if(!reflectorType.isAnnotationPresent(Reflector.class)) {
                throw new IllegalStateException("Missing @Reflector annotation");
            }
            final var annotation = reflectorType.getAnnotation(Reflector.class);
            PDAMod.LOGGER.debug("Initialized state reflector {} for @{}", reflector, annotation.value().getName());
            return Pair.of(annotation.value(), reflector);
        })
        .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    // @formatter:on

    protected final ConcurrentHashMap<String, ConcurrentHashMap<String, MutableState<?>>> fields = new ConcurrentHashMap<>();
    protected final Session session;

    public DefaultStateHandler(final Session session) {
        this.session = session;
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

    protected StateReflector getReflector(final Class<?> type) {
        final var annotations = type.getAnnotations();
        for (final var annotation : annotations) {
            final var reflector = REFLECTORS.get(annotation.annotationType());
            if (reflector == null) {
                continue;
            }
            return reflector;
        }
        return DefaultStateReflector.INSTANCE;
    }

    protected ConcurrentHashMap<String, MutableState<?>> getOrCreateProps(final String ownerId) {
        return fields.computeIfAbsent(ownerId, id -> new ConcurrentHashMap<>());
    }

    protected void addProperty(final String ownerId, final MutableState<?> property) {
        final var properties = getOrCreateProps(ownerId);
        if (properties.containsValue(property)) {
            throw new IllegalStateException("Property already registered");
        }
        properties.put(property.getName(), property);
    }

    protected void removeProperty(final String ownerId, final MutableState<?> property) {
        final var properties = fields.get(ownerId);
        if (properties == null || properties.remove(property.getName()) == null) {
            throw new IllegalArgumentException("No such property");
        }
        if (properties.isEmpty()) {
            fields.remove(ownerId); // Free up space we don't need for this owner
        }
    }
}
