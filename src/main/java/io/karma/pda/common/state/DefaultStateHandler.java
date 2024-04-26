/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.state;

import io.karma.pda.api.common.event.RegisterStateReflectorsEvent;
import io.karma.pda.api.common.session.Session;
import io.karma.pda.api.common.state.MutableState;
import io.karma.pda.api.common.state.State;
import io.karma.pda.api.common.state.StateHandler;
import io.karma.pda.api.common.state.StateReflector;
import io.karma.pda.api.common.util.Identifiable;
import net.minecraftforge.common.MinecraftForge;

import java.lang.annotation.Annotation;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * @author Alexander Hinze
 * @since 24/04/2024
 */
public class DefaultStateHandler implements StateHandler {
    protected final ConcurrentHashMap<String, ConcurrentHashMap<String, MutableState<?>>> fields = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<Class<? extends Annotation>, StateReflector> reflectors = new ConcurrentHashMap<>();
    protected final Session session;

    public DefaultStateHandler(final Session session) {
        this.session = session;
        MinecraftForge.EVENT_BUS.post(new RegisterStateReflectorsEvent(this));
    }

    @Override
    public void registerReflector(final Class<? extends Annotation> annotationType, final StateReflector reflector) {
        if (reflectors.containsKey(annotationType)) {
            throw new IllegalArgumentException("Reflector type already exists");
        }
        reflectors.put(annotationType, reflector);
    }

    @Override
    public void unregisterReflector(final Class<? extends Annotation> annotationType) {
        if (reflectors.remove(annotationType) == null) {
            throw new IllegalArgumentException("Reflector type doesn't exist");
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

    protected StateReflector getReflector(final Class<?> type) {
        final var annotations = type.getAnnotations();
        for (final var annotation : annotations) {
            final var reflector = reflectors.get(annotation.annotationType());
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
    }
}
