package io.karma.pda.api;

import io.karma.pda.api.app.App;
import io.karma.pda.api.util.Constants;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public final class API {
    // @formatter:off
    private API() {}
    // @formatter:on

    @SuppressWarnings("all")
    public static IForgeRegistry<App> getAppRegistry() {
        return RegistryManager.ACTIVE.getRegistry(Constants.APP_REGISTRY_NAME);
    }
}
