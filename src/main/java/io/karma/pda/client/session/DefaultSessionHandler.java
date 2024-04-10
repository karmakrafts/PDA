/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.session;

import io.karma.pda.api.client.session.SessionHandler;
import io.karma.pda.api.common.session.MuxedSession;
import io.karma.pda.api.common.session.Session;
import io.karma.pda.api.common.session.SessionContext;
import io.karma.pda.api.common.session.SessionType;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.network.SPacketCreateSession;
import io.karma.pda.common.network.SPacketTerminateSession;
import io.karma.pda.common.session.DefaultSession;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultSessionHandler implements SessionHandler {
    private Session session;

    @Override
    public Session createSession(final SessionContext context) {
        // @formatter:off
        final var contextData = context.getType() == SessionType.DOCKED
            ? context.getPos()
            : context.getHand();
        // @formatter:on
        final var uuid = UUID.randomUUID();
        PDAMod.CHANNEL.sendToServer(new SPacketCreateSession(context.getType(), uuid, contextData));
        PDAMod.LOGGER.debug("Created new session {} on CLIENT", uuid);
        return new DefaultSession(uuid, context);
    }

    @Override
    public void terminateSession(final Session session) {
        if (session instanceof MuxedSession<?> muxedSession) {
            muxedSession.getTargets().forEach(this::terminateSession);
            return;
        }
        final var uuid = session.getUUID();
        PDAMod.CHANNEL.sendToServer(new SPacketTerminateSession(uuid));
        PDAMod.LOGGER.debug("Terminated session {} on CLIENT", uuid);
    }

    @Nullable
    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public void setSession(final Session session) {
        this.session = session;
    }
}
