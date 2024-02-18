package io.karma.pda.api.common.app.component;

import net.minecraftforge.registries.ObjectHolder;

/**
 * @author Alexander Hinze
 * @since 18/02/2024
 */
public final class DefaultComponents {
    @ObjectHolder(value = "pda:container", registryName = "pda:components")
    public static final ComponentType<Component> CONTAINER = null;
    @ObjectHolder(value = "pda:label", registryName = "pda:components")
    public static final ComponentType<Component> LABEL = null;

    // @formatter:off
    private DefaultComponents() {}
    // @formatter:on
}
