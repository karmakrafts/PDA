/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.session;

import io.karma.pda.api.common.session.SessionContext;
import io.karma.pda.api.common.sync.Synchronizer;
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
    public ClientSession(final UUID id, final SessionContext context) {
        super(id, context);
    }

    @Override
    public Synchronizer getSynchronizer() {
        return new ClientSynchronizer(id);
    }
}
