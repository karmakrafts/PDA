package io.karma.pda.api.common.app;

import net.minecraftforge.registries.ObjectHolder;

/**
 * @author Alexander Hinze
 * @since 18/02/2024
 */
public final class DefaultApps {
    @ObjectHolder(value = "pda:launcher", registryName = "pda:apps")
    public static final AppType<App> LAUNCHER = null;

    // @formatter:off
    private DefaultApps() {}
    // @formatter:on
}
