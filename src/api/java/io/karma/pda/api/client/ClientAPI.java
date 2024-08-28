/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client;

import io.karma.pda.api.client.flex.FlexNodeHandler;
import io.karma.pda.api.client.render.display.DisplayRenderer;
import io.karma.pda.api.client.render.shader.ShaderHandler;
import io.karma.pda.api.session.SessionHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ClientAPI {
    private static final Logger INTERNAL_LOGGER = LogManager.getLogger("PDA Client API");

    private static SessionHandler sessionHandler;
    private static FlexNodeHandler flexNodeHandler;
    private static DisplayRenderer displayRenderer;
    private static ShaderHandler shaderHandler;
    private static boolean isInitialized;

    // @formatter:off
    private ClientAPI() {}
    // @formatter:on

    @Internal
    public static void init() {
        if (isInitialized) {
            throw new IllegalStateException("Already initialized");
        }
        INTERNAL_LOGGER.info("PONG! Client API is initialized");
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

    @Internal
    public static void setSessionHandler(final SessionHandler sessionHandler) {
        ClientAPI.sessionHandler = sessionHandler;
    }

    public static DisplayRenderer getDisplayRenderer() {
        assertInitialized();
        return displayRenderer;
    }

    @Internal
    public static void setDisplayRenderer(final DisplayRenderer displayRenderer) {
        ClientAPI.displayRenderer = displayRenderer;
    }

    public static FlexNodeHandler getFlexNodeHandler() {
        assertInitialized();
        return flexNodeHandler;
    }

    @Internal
    public static void setFlexNodeHandler(final FlexNodeHandler flexNodeHandler) {
        ClientAPI.flexNodeHandler = flexNodeHandler;
    }

    public static ShaderHandler getShaderHandler() {
        return shaderHandler;
    }

    @Internal
    public static void setShaderHandler(final ShaderHandler shaderHandler) {
        ClientAPI.shaderHandler = shaderHandler;
    }
}
