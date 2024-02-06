package io.karma.pda.common.util;

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

    public static boolean getOrDefault(final @Nullable CompoundTag tag, final String key, final boolean defaultValue) {
        if (tag == null || !tag.contains(key)) {
            return defaultValue;
        }
        return tag.getBoolean(key);
    }
}
