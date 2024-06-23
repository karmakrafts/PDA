/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client;

import io.karma.pda.api.client.flex.FlexNodeHandler;
import io.karma.pda.api.client.render.display.DisplayRenderer;
import io.karma.pda.api.client.render.shader.ShaderFactory;
import io.karma.pda.api.client.render.shader.ShaderPreProcessor;
import io.karma.pda.api.session.SessionHandler;
import io.karma.pda.api.util.FloatSupplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ClientAPI {
    private static SessionHandler sessionHandler;
    private static FlexNodeHandler flexNodeHandler;
    private static DisplayRenderer displayRenderer;
    private static FloatSupplier shaderTimeProvider;
    private static ShaderFactory shaderFactory;
    private static Supplier<ShaderPreProcessor> shaderPreProcessorSupplier;
    private static boolean isInitialized;

    // @formatter:off
    private ClientAPI() {}
    // @formatter:on

    @ApiStatus.Internal
    public static void init() {
        if (isInitialized) {
            throw new IllegalStateException("Already initialized");
        }
        isInitialized = true;
    }

    private static void assertInitialized() {
        if (!isInitialized) {
            throw new IllegalStateException("Not initialized");
        }
    }

    public static SessionHandler getSessionHandler() {
        assertInitialized();
        return sessionHandler;
    }

    @ApiStatus.Internal
    public static void setSessionHandler(final SessionHandler sessionHandler) {
        ClientAPI.sessionHandler = sessionHandler;
    }

    public static DisplayRenderer getDisplayRenderer() {
        assertInitialized();
        return displayRenderer;
    }

    @ApiStatus.Internal
    public static void setDisplayRenderer(final DisplayRenderer displayRenderer) {
        ClientAPI.displayRenderer = displayRenderer;
    }

    public static FlexNodeHandler getFlexNodeHandler() {
        assertInitialized();
        return flexNodeHandler;
    }

    @ApiStatus.Internal
    public static void setFlexNodeHandler(final FlexNodeHandler flexNodeHandler) {
        ClientAPI.flexNodeHandler = flexNodeHandler;
    }

    public static float getShaderTime() {
        return shaderTimeProvider.get();
    }

    @ApiStatus.Internal
    public static void setShaderTimeProvider(final FloatSupplier shaderTimeProvider) {
        ClientAPI.shaderTimeProvider = shaderTimeProvider;
    }

    public static ShaderFactory getShaderFactory() {
        return shaderFactory;
    }

    @ApiStatus.Internal
    public static void setShaderFactory(final ShaderFactory shaderFactory) {
        ClientAPI.shaderFactory = shaderFactory;
    }

    public static ShaderPreProcessor getShaderPreProcessor() {
        return shaderPreProcessorSupplier.get();
    }

    @ApiStatus.Internal
    public static void setShaderPreProcessorSupplier(final Supplier<ShaderPreProcessor> shaderPreProcessorSupplier) {
        ClientAPI.shaderPreProcessorSupplier = shaderPreProcessorSupplier;
    }
}
