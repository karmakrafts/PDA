/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.state;

import io.karma.pda.api.state.Reflector;
import io.karma.pda.api.state.StateReflector;
import io.karma.pda.api.util.LogMarkers;
import io.karma.pda.mod.PDAMod;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Alexander Hinze
 * @since 13/08/2024
 */
public final class Reflectors {
    // @formatter:off
    private static final Map<Class<? extends Annotation>, StateReflector> REFLECTORS = PDAMod.STATE_REFLECTORS.stream()
        .map(p -> {
            final var reflector = p.get();
            final var reflectorType = reflector.getClass();
            if(!reflectorType.isAnnotationPresent(Reflector.class)) {
                throw new IllegalStateException("Missing @Reflector annotation");
            }
            final var annotation = reflectorType.getAnnotation(Reflector.class);
            PDAMod.LOGGER.debug(LogMarkers.PROTOCOL, "Initialized state reflector {} for @{}", reflector, annotation.value().getName());
            return Pair.of(annotation.value(), reflector);
        })
        .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

    private Reflectors() {}
    // @formatter:on

    public static Map<Class<? extends Annotation>, StateReflector> get() {
        return REFLECTORS;
    }
}
