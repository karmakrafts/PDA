/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.state;

import io.karma.pda.api.state.MutableState;
import io.karma.pda.api.state.Persistent;
import io.karma.pda.api.state.StateReflector;
import io.karma.pda.api.state.Synchronize;
import io.karma.pda.api.util.Exceptions;
import io.karma.pda.common.PDAMod;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Alexander Hinze
 * @since 26/04/2024
 */
public final class DefaultStateReflector implements StateReflector {
    public static final DefaultStateReflector INSTANCE = new DefaultStateReflector();
    private static final ConcurrentHashMap<Class<?>, List<Field>> FIELD_HANDLE_CACHE = new ConcurrentHashMap<>();

    // @formatter:off
    private DefaultStateReflector() {}
    // @formatter:on

    private static boolean isSyncedField(final Field field) {
        return MutableState.class.isAssignableFrom(field.getType()) && field.isAnnotationPresent(Synchronize.class);
    }

    private static MutableState<?> getState(final Object instance, final Field field) {
        try {
            final var name = field.getName();
            PDAMod.LOGGER.debug("Reflecting synced property '{}' in {}", name, field.getDeclaringClass().getName());
            final var isNonPublic = !Modifier.isPublic(field.getModifiers());
            if (isNonPublic) {
                field.setAccessible(true);
            }
            final var state = (MutableState<?>) field.get(instance);
            if (isNonPublic) {
                field.setAccessible(false);
            }
            state.setName(name);
            state.setPersistent(field.isAnnotationPresent(Persistent.class));
            return state;
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error("Could not retrieve field pair for '{}' in {}: {}",
                field.getName(),
                field.getDeclaringClass().getName(),
                Exceptions.toFancyString(error));
            return null;
        }
    }

    private static List<Field> findFields(final @Nullable Class<?> type) {
        if (type == null || type == Object.class) {
            return Collections.emptyList();
        }
        // @formatter:off
        return FIELD_HANDLE_CACHE.computeIfAbsent(type, t -> Arrays.stream(type.getDeclaredFields())
            .filter(DefaultStateReflector::isSyncedField)
            .toList());
        // @formatter:on
    }

    @Override
    public List<? extends MutableState<?>> getStates(final Class<?> type, final Object instance,
                                                     final Function<Class<?>, StateReflector> reflectorGetter) {
        final var superType = type.getSuperclass();
        final var stream = findFields(type).stream().map(p -> getState(instance, p));
        if (superType == null || superType == Object.class) {
            return stream.toList();
        }
        // @formatter:off
        return Stream.concat(stream, reflectorGetter.apply(superType)
            .getStates(superType, instance, reflectorGetter)
            .stream()).toList();
        // @formatter:on
    }
}
