/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import io.karma.pda.api.common.util.Constants;
import net.minecraftforge.registries.ObjectHolder;

/**
 * @author Alexander Hinze
 * @since 18/02/2024
 */
public final class DefaultComponents {
    private static final String REGISTRY_NAME = Constants.MODID + ":components";

    @ObjectHolder(value = Constants.MODID + ":container", registryName = REGISTRY_NAME)
    public static final ComponentType<DefaultContainer> CONTAINER = ComponentType.nullType();
    @ObjectHolder(value = Constants.MODID + ":label", registryName = REGISTRY_NAME)
    public static final ComponentType<Label> LABEL = ComponentType.nullType();

    // @formatter:off
    private DefaultComponents() {}
    // @formatter:on
}
