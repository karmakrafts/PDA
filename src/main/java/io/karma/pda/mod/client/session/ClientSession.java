/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.session;

import io.karma.pda.api.app.App;
import io.karma.pda.api.app.AppType;
import io.karma.pda.api.app.Launcher;
import io.karma.pda.api.client.render.app.AppRenderers;
import io.karma.pda.api.session.Session;
import io.karma.pda.api.session.SessionContext;
import io.karma.pda.api.state.StateHandler;
import io.karma.pda.api.util.LogMarkers;
import io.karma.pda.mod.PDAMod;
import io.karma.pda.mod.client.app.ClientLauncher;
import io.karma.pda.mod.client.state.ClientStateHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.time.Instant;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ClientSession implements Session {
    private final ClientStateHandler stateHandler = new ClientStateHandler(this);
    private final ClientLauncher launcher = new ClientLauncher(this);
    private final UUID id;
    private final SessionContext context;
    private final Instant creationTime;

    public ClientSession(final UUID id, final SessionContext context) {
        this.id = id;
        this.context = context;
        creationTime = Instant.now();
    }

    @Override
    public SessionContext getContext() {
        return context;
    }

    @Override
    public Instant getCreationTime() {
        return creationTime;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public Launcher getLauncher() {
        return launcher;
    }

    @Override
    public StateHandler getStateHandler() {
        return stateHandler;
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
