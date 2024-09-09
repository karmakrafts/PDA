/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.karma.pda.api.app.AppType;
import io.karma.pda.api.app.component.ComponentType;
import io.karma.pda.api.app.theme.Theme;
import io.karma.pda.api.display.DisplayModeSpec;
import io.karma.pda.api.session.SessionHandler;
import io.karma.pda.api.util.Constants;
import io.karma.peregrine.api.font.FontFamily;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public class API {
    private static final Logger INTERNAL_LOGGER = LogManager.getLogger("PDA API");

    private static Logger logger;
    private static ExecutorService executorService;
    private static SessionHandler sessionHandler;
    private static ObjectMapper objectMapper;
    private static Supplier<IForgeRegistry<ComponentType<?>>> componentTypeRegistry;
    private static Supplier<IForgeRegistry<AppType<?>>> appTypeRegistry;
    private static Supplier<IForgeRegistry<Theme>> themeRegistry;
    private static Supplier<IForgeRegistry<FontFamily>> fontFamilyRegistry;
    private static Supplier<IForgeRegistry<DisplayModeSpec>> displayModeRegistry;
    private static boolean isInitialized;

    // @formatter:off
    @Internal
    protected API() {}
    // @formatter:on

    @Internal
    public static void init() {
        if (isInitialized) {
            throw new IllegalStateException("Already initialized");
        }
        INTERNAL_LOGGER.info("PONG! API is initialized");
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

    @Internal
    public static Logger getLogger() {
        assertInitialized();
        return logger;
    }

    @Internal
    public static void setLogger(final Logger logger) {
        API.logger = logger;
    }

    public static ObjectMapper getObjectMapper() {
        assertInitialized();
        return objectMapper;
    }

    @Internal
    public static void setObjectMapper(final ObjectMapper objectMapper) {
        API.objectMapper = objectMapper;
    }

    public static ResourceManager getResourceManager() {
        return DistExecutor.unsafeRunForDist(() -> () -> Minecraft.getInstance().getResourceManager(),
            () -> () -> ServerLifecycleHooks.getCurrentServer().getResourceManager());
    }

    public static SessionHandler getSessionHandler() {
        assertInitialized();
        return sessionHandler;
    }

    @Internal
    public static void setSessionHandler(final SessionHandler sessionHandler) {
        API.sessionHandler = sessionHandler;
    }

    public static ExecutorService getExecutorService() {
        assertInitialized();
        return executorService;
    }

    @Internal
    public static void setExecutorService(final ExecutorService executorService) {
        API.executorService = executorService;
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

    public static DeferredRegister<FontFamily> makeFontFamilyRegister(final String modId) {
        return DeferredRegister.create(Constants.FONT_FAMILY_REGISTRY_NAME, modId);
    }

    public static DeferredRegister<DisplayModeSpec> makeDisplayModeRegister(final String modId) {
        return DeferredRegister.create(Constants.DISPLAY_MODE_REGISTRY_NAME, modId);
    }

    @SuppressWarnings("all")
    public static IForgeRegistry<ComponentType<?>> getComponentTypeRegistry() {
        assertInitialized();
        return componentTypeRegistry.get();
    }

    @Internal
    public static void setComponentTypeRegistry(final Supplier<IForgeRegistry<ComponentType<?>>> componentTypeRegistry) {
        API.componentTypeRegistry = componentTypeRegistry;
    }

    public static IForgeRegistry<AppType<?>> getAppTypeRegistry() {
        assertInitialized();
        return appTypeRegistry.get();
    }

    @Internal
    public static void setAppTypeRegistry(final Supplier<IForgeRegistry<AppType<?>>> appTypeRegistry) {
        API.appTypeRegistry = appTypeRegistry;
    }

    public static IForgeRegistry<Theme> getThemeRegistry() {
        assertInitialized();
        return themeRegistry.get();
    }

    @Internal
    public static void setThemeRegistry(final Supplier<IForgeRegistry<Theme>> themeRegistry) {
        API.themeRegistry = themeRegistry;
    }

    public static IForgeRegistry<FontFamily> getFontFamilyRegistry() {
        assertInitialized();
        return fontFamilyRegistry.get();
    }

    @Internal
    public static void setFontFamilyRegistry(final Supplier<IForgeRegistry<FontFamily>> fontFamilyRegistry) {
        API.fontFamilyRegistry = fontFamilyRegistry;
    }

    public static IForgeRegistry<DisplayModeSpec> getDisplayModeRegistry() {
        assertInitialized();
        return displayModeRegistry.get();
    }

    @Internal
    public static void setDisplayModeRegistry(final Supplier<IForgeRegistry<DisplayModeSpec>> displayModeRegistry) {
        API.displayModeRegistry = displayModeRegistry;
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

    public static Collection<FontFamily> getFontFamilies() {
        assertInitialized();
        return fontFamilyRegistry.get().getValues();
    }

    public static Collection<DisplayModeSpec> getDisplayModes() {
        assertInitialized();
        return displayModeRegistry.get().getValues();
    }
}
