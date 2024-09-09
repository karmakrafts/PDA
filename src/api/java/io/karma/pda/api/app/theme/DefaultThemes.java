/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.app.theme;

import io.karma.pda.api.util.Constants;
import net.minecraftforge.registries.ObjectHolder;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public final class DefaultThemes {
    public static final String REGISTRY_NAME = Constants.MODID + ":themes";

    @ObjectHolder(value = Constants.MODID + ":default_dark", registryName = REGISTRY_NAME)
    public static final Theme DEFAULT_DARK = Theme.nullType();
    @ObjectHolder(value = Constants.MODID + ":default_light", registryName = REGISTRY_NAME)
    public static final Theme DEFAULT_LIGHT = Theme.nullType();

    // @formatter:off
    private DefaultThemes() {}
    // @formatter:on
}
