/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.sync;

import io.karma.pda.api.common.API;

import java.util.HashMap;

/**
 * @author Alexander Hinze
 * @since 18/04/2024
 */
public final class SyncCodecCache {
    private static final HashMap<Class<? extends SyncCodec<?>>, SyncCodec<?>> codecs = new HashMap<>();

    // @formatter:off
    private SyncCodecCache() {}
    // @formatter:on

    @SuppressWarnings("unchecked")
    public static <T, C extends SyncCodec<T>> C getCodec(final Class<T> valueType, final Class<C> type) {
        return (C) codecs.computeIfAbsent(type, t -> {
            try {
                final var instance = t.getConstructor().newInstance();
                if (!valueType.isAssignableFrom(instance.getType())) {
                    throw new IllegalStateException("Mismatched value type");
                }
                return instance;
            }
            catch (Throwable error) {
                API.getLogger().error("Could not create sync codec instance: {}", error.getMessage());
                return null;
            }
        });
    }
}
