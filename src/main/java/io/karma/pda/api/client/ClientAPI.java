/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client;

import io.karma.pda.api.client.flex.FlexNodeHandler;
import io.karma.pda.api.client.render.gfx.BrushFactory;
import io.karma.pda.api.client.render.gfx.DefaultBrushes;
import io.karma.pda.api.client.session.SessionHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ClientAPI {
    private static SessionHandler sessionHandler;
    private static FlexNodeHandler flexNodeHandler;
    private static BrushFactory brushFactory;

    // @formatter:off
    private ClientAPI() {}
    // @formatter:on

    @ApiStatus.Internal
    public static void init() {
        DefaultBrushes.setup(brushFactory);
    }

    @ApiStatus.Internal
    public static void setSessionHandler(final SessionHandler sessionHandler) {
        ClientAPI.sessionHandler = sessionHandler;
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
        return sessionHandler;
    }

    public static FlexNodeHandler getFlexNodeHandler() {
        return flexNodeHandler;
    }

    public static BrushFactory getBrushFactory() {
        return brushFactory;
    }
}
