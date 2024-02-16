package io.karma.pda.api;

import io.karma.pda.api.app.AppType;
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

    public static DeferredRegister<AppType<?>> makeDeferredAppTypeRegister(final String modId) {
        return DeferredRegister.create(Constants.APP_REGISTRY_NAME, modId);
    }

    @SuppressWarnings("all")
    public static IForgeRegistry<AppType<?>> getAppTypeRegistry() {
        final var registry = RegistryManager.ACTIVE.<AppType<?>>getRegistry(Constants.APP_REGISTRY_NAME);
        if (registry != null) {
            return registry;
        }
        return RegistryManager.FROZEN.getRegistry(Constants.APP_REGISTRY_NAME);
    }

    @SuppressWarnings("all")
    public static Collection<AppType<?>> getAppTypes() {
        return getAppTypeRegistry().getValues();
    }
}
