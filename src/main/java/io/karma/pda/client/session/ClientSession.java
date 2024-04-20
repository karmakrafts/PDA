/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.session;

import io.karma.pda.api.client.render.app.AppRenderers;
import io.karma.pda.api.common.app.App;
import io.karma.pda.api.common.app.AppType;
import io.karma.pda.api.common.session.SessionContext;
import io.karma.pda.api.common.sync.Synchronizer;
import io.karma.pda.client.app.ClientLauncher;
import io.karma.pda.client.sync.ClientSynchronizer;
import io.karma.pda.common.session.DefaultSession;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ClientSession extends DefaultSession {
    private final ClientSynchronizer synchronizer;

    public ClientSession(final UUID id, final SessionContext context) {
        super(id, context, ClientLauncher::new);
        synchronizer = new ClientSynchronizer(id);
    }

    @Override
    public Synchronizer getSynchronizer() {
        return synchronizer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onTermination() {
        // Invoke cleanup callback for all active/suspended app renderers
        for (final var app : launcher.getOpenApps()) {
            AppRenderers.get((AppType<App>) app.getType()).cleanup(app);
        }
    }
}
