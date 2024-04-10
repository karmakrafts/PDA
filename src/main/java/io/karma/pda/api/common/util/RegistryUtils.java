/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

/**
 * @author Alexander Hinze
 * @since 17/02/2024
 */
public final class RegistryUtils {
    // @formatter:off
    private RegistryUtils() {}
    // @formatter:on

    @SuppressWarnings("all")
    public static <T> IForgeRegistry<T> getRegistry(final ResourceLocation name) {
        final var registry = RegistryManager.ACTIVE.<T>getRegistry(name);
        if (registry != null) {
            return registry;
        }
        return RegistryManager.FROZEN.getRegistry(name);
    }
}
