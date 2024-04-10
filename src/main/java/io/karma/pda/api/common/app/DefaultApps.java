/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app;

import io.karma.pda.api.common.util.Constants;
import net.minecraftforge.registries.ObjectHolder;

/**
 * @author Alexander Hinze
 * @since 18/02/2024
 */
public final class DefaultApps {
    @ObjectHolder(value = Constants.MODID + ":launcher", registryName = REGISTRY_NAME)
    public static final AppType<?> LAUNCHER = null;
    private static final String REGISTRY_NAME = Constants.MODID + ":apps";

    // @formatter:off
    private DefaultApps() {}
    // @formatter:on
}
