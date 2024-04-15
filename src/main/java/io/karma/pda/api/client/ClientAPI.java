/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client;

import io.karma.pda.api.client.flex.FlexNodeHandler;
import io.karma.pda.api.client.render.gfx.BrushFactory;
import io.karma.pda.api.client.render.gfx.DefaultBrushes;
import io.karma.pda.api.common.session.SessionHandler;
import io.karma.pda.api.common.sync.Synchronizer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;
import java.util.function.Function;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ClientAPI {
    private static SessionHandler sessionHandler;
    private static Function<UUID, Synchronizer> synchronizerFactory;
    private static FlexNodeHandler flexNodeHandler;
    private static BrushFactory brushFactory;
    private static boolean isInitialized;

    // @formatter:off
    private ClientAPI() {}
    // @formatter:on

    @ApiStatus.Internal
    public static void init() {
        if (isInitialized) {
            throw new IllegalStateException("Already initialized");
        }
        DefaultBrushes.setup(brushFactory);
        isInitialized = true;
    }

    private static void assertInitialized() {
        if (!isInitialized) {
            throw new IllegalStateException("Not initialized");
        }
    }

    @ApiStatus.Internal
    public static void setSessionHandler(final SessionHandler sessionHandler) {
        ClientAPI.sessionHandler = sessionHandler;
    }

    @ApiStatus.Internal
    public static void setSynchronizerFactory(final Function<UUID, Synchronizer> synchronizerFactory) {
        ClientAPI.synchronizerFactory = synchronizerFactory;
    }

    @ApiStatus.Internal
    public static void setFlexNodeHandler(final FlexNodeHandler flexNodeHandler) {
        ClientAPI.flexNodeHandler = flexNodeHandler;
    }

    @ApiStatus.Internal
    public static void setBrushFactory(final BrushFactory brushFactory) {
        ClientAPI.brushFactory = brushFactory;
    }

    public static SessionHandler getSessionHandler() {
        assertInitialized();
        return sessionHandler;
    }

    public static FlexNodeHandler getFlexNodeHandler() {
        assertInitialized();
        return flexNodeHandler;
    }

    public static BrushFactory getBrushFactory() {
        assertInitialized();
        return brushFactory;
    }

    public static Synchronizer createSynchronizer(final UUID id) {
        assertInitialized();
        return synchronizerFactory.apply(id);
    }
}
