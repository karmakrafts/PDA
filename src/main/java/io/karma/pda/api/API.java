package io.karma.pda.api;

import io.karma.pda.api.app.App;
import io.karma.pda.api.util.Constants;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

import java.util.Collection;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public final class API {
    // @formatter:off
    private API() {}
    // @formatter:on

    public static DeferredRegister<App> makeDeferredAppRegister(final String modId) {
        return DeferredRegister.create(Constants.APP_REGISTRY_NAME, modId);
    }

    @SuppressWarnings("all")
    public static IForgeRegistry<App> getAppRegistry() {
        final var registry = RegistryManager.ACTIVE.<App>getRegistry(Constants.APP_REGISTRY_NAME);
        if (registry != null) {
            return registry;
        }
        return RegistryManager.FROZEN.getRegistry(Constants.APP_REGISTRY_NAME);
    }

    @SuppressWarnings("all")
    public static Collection<App> getApps() {
        return getAppRegistry().getValues();
    }
}
