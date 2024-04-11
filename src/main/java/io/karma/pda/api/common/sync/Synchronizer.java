/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.sync;

import io.karma.pda.api.common.API;
import io.karma.pda.api.common.util.Identifiable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Modifier;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
public interface Synchronizer {
    void register(final UUID id, final Synced<?> value);

    void flush();

    default void registerAll(final Identifiable object) {
        try {
            final var ownerId = object.getId();
            final var type = object.getClass();
            final var lookup = MethodHandles.privateLookupIn(type, MethodHandles.lookup());
            final var instance = object.getClass().cast(object); // Runtime-cast to aid field handle invocation
            final var fields = type.getDeclaredFields();
            for (final var field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue; // We don't care about static fields
                }
                final var fieldType = field.getType();
                if (!Synced.class.isAssignableFrom(fieldType)) {
                    continue; // We are not interested in this field
                }
                final var fieldHandle = lookup.unreflectVarHandle(field);
                final var fieldValue = (Synced<?>) fieldHandle.get(instance);
                if (fieldValue == null) {
                    API.getLogger().warn("Uninitialized synchronized field, skipping {} in {}",
                        field.getName(),
                        object);
                    continue;
                }
                register(ownerId, fieldValue);
            }
        }
        catch (Throwable error) {
            API.getLogger().error("Could not register synchronizable fields in {}", object);
        }
    }
}
