/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common;

import io.karma.pda.api.common.app.AppType;
import io.karma.pda.api.common.app.component.ComponentType;
import io.karma.pda.api.common.util.Constants;
import io.karma.pda.api.common.util.RegistryUtils;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collection;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public final class API {
    // @formatter:off
    private API() {}
    // @formatter:on

    public static DeferredRegister<ComponentType<?>> makeDeferredComponentTypeRegister(final String modId) {
        return DeferredRegister.create(Constants.COMPONENT_REGISTRY_NAME, modId);
    }

    public static DeferredRegister<AppType<?>> makeDeferredAppTypeRegister(final String modId) {
        return DeferredRegister.create(Constants.APP_REGISTRY_NAME, modId);
    }

    @SuppressWarnings("all")
    public static IForgeRegistry<ComponentType<?>> getComponentTypeRegistry() {
        return RegistryUtils.getRegistry(Constants.COMPONENT_REGISTRY_NAME);
    }

    public static IForgeRegistry<AppType<?>> getAppTypeRegistry() {
        return RegistryUtils.getRegistry(Constants.APP_REGISTRY_NAME);
    }

    @SuppressWarnings("all")
    public static Collection<ComponentType<?>> getComponentTypes() {
        return getComponentTypeRegistry().getValues();
    }

    @SuppressWarnings("all")
    public static Collection<AppType<?>> getAppTypes() {
        return getAppTypeRegistry().getValues();
    }
}
