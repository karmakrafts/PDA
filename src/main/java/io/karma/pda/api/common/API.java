/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common;

import io.karma.pda.api.common.app.AppType;
import io.karma.pda.api.common.app.component.ComponentType;
import io.karma.pda.api.common.util.Constants;
import io.karma.pda.api.common.util.RegistryUtils;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public class API {
    private static Logger logger;
    private static ExecutorService executorService;

    // @formatter:off
    @ApiStatus.Internal
    protected API() {}
    // @formatter:on

    @ApiStatus.Internal
    public static void setLogger(Logger logger) {
        API.logger = logger;
    }

    @ApiStatus.Internal
    public static void setExecutorService(final ExecutorService executorService) {
        API.executorService = executorService;
    }

    @ApiStatus.Internal
    public static Logger getLogger() {
        return logger;
    }

    public static ExecutorService getExecutorService() {
        return executorService;
    }

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
