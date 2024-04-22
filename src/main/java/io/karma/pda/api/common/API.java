/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.karma.pda.api.common.app.AppType;
import io.karma.pda.api.common.app.component.ComponentType;
import io.karma.pda.api.common.app.theme.Theme;
import io.karma.pda.api.common.session.SessionHandler;
import io.karma.pda.api.common.util.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public class API {
    private static Logger logger;
    private static ExecutorService executorService;
    private static SessionHandler sessionHandler;
    private static ObjectMapper objectMapper;
    private static Supplier<IForgeRegistry<ComponentType<?>>> componentTypeRegistry;
    private static Supplier<IForgeRegistry<AppType<?>>> appTypeRegistry;
    private static Supplier<IForgeRegistry<Theme>> themeRegistry;
    private static boolean isInitialized;

    // @formatter:off
    @ApiStatus.Internal
    protected API() {}
    // @formatter:on

    @ApiStatus.Internal
    public static void init() {
        if (isInitialized) {
            throw new IllegalStateException("Already initialized");
        }
        logger.info("Initializing API");
        isInitialized = true;
    }

    public static boolean isInitialized() {
        return isInitialized;
    }

    private static void assertInitialized() {
        if (!isInitialized) {
            throw new IllegalStateException("Not initialized");
        }
    }

    @ApiStatus.Internal
    public static void setLogger(final Logger logger) {
        API.logger = logger;
    }

    @ApiStatus.Internal
    public static void setObjectMapper(final ObjectMapper objectMapper) {
        API.objectMapper = objectMapper;
    }

    @ApiStatus.Internal
    public static void setExecutorService(final ExecutorService executorService) {
        API.executorService = executorService;
    }

    @ApiStatus.Internal
    public static void setSessionHandler(final SessionHandler sessionHandler) {
        API.sessionHandler = sessionHandler;
    }

    @ApiStatus.Internal
    public static void setComponentTypeRegistry(
        final Supplier<IForgeRegistry<ComponentType<?>>> componentTypeRegistry) {
        API.componentTypeRegistry = componentTypeRegistry;
    }

    @ApiStatus.Internal
    public static void setAppTypeRegistry(final Supplier<IForgeRegistry<AppType<?>>> appTypeRegistry) {
        API.appTypeRegistry = appTypeRegistry;
    }

    @ApiStatus.Internal
    public static void setThemeRegistry(final Supplier<IForgeRegistry<Theme>> themeRegistry) {
        API.themeRegistry = themeRegistry;
    }

    @ApiStatus.Internal
    public static Logger getLogger() {
        assertInitialized();
        return logger;
    }

    public static ObjectMapper getObjectMapper() {
        assertInitialized();
        return objectMapper;
    }

    public static ResourceManager getResourceManager() {
        return DistExecutor.unsafeRunForDist(() -> () -> Minecraft.getInstance().getResourceManager(),
            () -> () -> ServerLifecycleHooks.getCurrentServer().getResourceManager());
    }

    public static ExecutorService getExecutorService() {
        assertInitialized();
        return executorService;
    }

    public static SessionHandler getSessionHandler() {
        assertInitialized();
        return sessionHandler;
    }

    public static DeferredRegister<ComponentType<?>> makeDeferredComponentTypeRegister(final String modId) {
        return DeferredRegister.create(Constants.COMPONENT_REGISTRY_NAME, modId);
    }

    public static DeferredRegister<AppType<?>> makeDeferredAppTypeRegister(final String modId) {
        return DeferredRegister.create(Constants.APP_REGISTRY_NAME, modId);
    }

    public static DeferredRegister<Theme> makeThemeRegister(final String modId) {
        return DeferredRegister.create(Constants.THEME_REGISTRY_NAME, modId);
    }

    @SuppressWarnings("all")
    public static IForgeRegistry<ComponentType<?>> getComponentTypeRegistry() {
        assertInitialized();
        return componentTypeRegistry.get();
    }

    public static IForgeRegistry<AppType<?>> getAppTypeRegistry() {
        assertInitialized();
        return appTypeRegistry.get();
    }

    public static IForgeRegistry<Theme> getThemeRegistry() {
        assertInitialized();
        return themeRegistry.get();
    }

    @SuppressWarnings("all")
    public static Collection<ComponentType<?>> getComponentTypes() {
        assertInitialized();
        return componentTypeRegistry.get().getValues();
    }

    @SuppressWarnings("all")
    public static Collection<AppType<?>> getAppTypes() {
        assertInitialized();
        return appTypeRegistry.get().getValues();
    }

    public static Collection<Theme> getThemes() {
        assertInitialized();
        return themeRegistry.get().getValues();
    }
}
