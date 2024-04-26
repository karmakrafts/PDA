/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.session;

import io.karma.pda.api.client.render.app.AppRenderers;
import io.karma.pda.api.common.app.App;
import io.karma.pda.api.common.app.AppType;
import io.karma.pda.api.common.session.SessionContext;
import io.karma.pda.api.common.util.LogMarkers;
import io.karma.pda.client.app.ClientLauncher;
import io.karma.pda.client.state.ClientStateHandler;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.session.DefaultSession;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ClientSession extends DefaultSession {
    public ClientSession(final UUID id, final SessionContext context) {
        super(id, context, ClientStateHandler::new, ClientLauncher::new);
    }

    @Override
    public void onEstablished() {
        PDAMod.LOGGER.debug(LogMarkers.PROTOCOL, "Established session {} on client", id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onTerminated() {
        // Invoke cleanup callback for all active/suspended app renderers
        for (final var app : launcher.getOpenApps()) {
            Minecraft.getInstance().execute(() -> AppRenderers.get((AppType<App>) app.getType()).dispose(app));
        }
        PDAMod.LOGGER.debug(LogMarkers.PROTOCOL, "Terminated session {} on client", id);
    }
}
