/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.util;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 06/02/2024
 */
public final class NBTUtils {
    // @formatter:off
    private NBTUtils() {}
    // @formatter:on

    public static boolean contains(final @Nullable CompoundTag tag, final String key) {
        return tag != null && tag.contains(key);
    }

    public static void set(final @Nullable CompoundTag tag, final String key, final Enum<?> value) {
        if (tag == null) {
            return;
        }
        tag.putInt(key, value.ordinal());
    }

    public static <E extends Enum<E>> @Nullable E get(final @Nullable CompoundTag tag,
                                                      final String key,
                                                      final Class<E> type) {
        if (tag == null || !tag.contains(key)) {
            return null;
        }
        return type.getEnumConstants()[tag.getInt(key)];
    }

    public static <E extends Enum<E>> E getOrDefault(final @Nullable CompoundTag tag,
                                                     final String key,
                                                     final Class<E> type,
                                                     final E defaultValue) {
        if (tag == null || !tag.contains(key)) {
            return defaultValue;
        }
        return type.getEnumConstants()[tag.getInt(key)];
    }

    public static boolean getOrDefault(final @Nullable CompoundTag tag, final String key, final boolean defaultValue) {
        if (tag == null || !tag.contains(key)) {
            return defaultValue;
        }
        return tag.getBoolean(key);
    }

    public static int getOrDefault(final @Nullable CompoundTag tag, final String key, final int defaultValue) {
        if (tag == null || !tag.contains(key)) {
            return defaultValue;
        }
        return tag.getInt(key);
    }

    public static @Nullable String getOrDefault(final @Nullable CompoundTag tag,
                                                final String key,
                                                final @Nullable String defaultValue) {
        if (tag == null || !tag.contains(key)) {
            return defaultValue;
        }
        return tag.getString(key);
    }
}
