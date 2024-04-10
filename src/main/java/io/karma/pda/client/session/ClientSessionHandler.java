/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.session;

import io.karma.pda.api.client.session.SessionHandler;
import io.karma.pda.api.common.session.MuxedSession;
import io.karma.pda.api.common.session.Session;
import io.karma.pda.api.common.session.SessionContext;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.network.sb.SPacketCreateSession;
import io.karma.pda.common.network.sb.SPacketTerminateSession;
import io.karma.pda.common.session.DefaultSession;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ClientSessionHandler implements SessionHandler {
    public static final ClientSessionHandler INSTANCE = new ClientSessionHandler();
    private final ConcurrentHashMap<UUID, UUID> newlyCreatedSessions = new ConcurrentHashMap<>();
    private Session session;

    // @formatter:off
    private ClientSessionHandler() {}
    // @formatter:on

    private UUID waitForSessionId(final UUID requestId) {
        while (true) {
            final var sessionId = newlyCreatedSessions.get(requestId);
            if (sessionId != null) {
                return sessionId;
            }
            Thread.yield();
        }
    }

    @ApiStatus.Internal
    public void addNewSessionId(final UUID requestId, final UUID sessionId) {
        if (newlyCreatedSessions.containsKey(requestId)) {
            return; // TODO: add warning
        }
        newlyCreatedSessions.put(requestId, sessionId);
    }

    @Override
    public CompletableFuture<Session> createSession(final SessionContext context) {
        final var request = SPacketCreateSession.fromContext(context);
        final var requestId = request.getRequestId();
        PDAMod.CHANNEL.sendToServer(request);
        PDAMod.LOGGER.debug("Requested new session on CLIENT");
        return CompletableFuture.supplyAsync(() -> {
            PDAMod.LOGGER.debug("Waiting for response asynchronously..");
            final var sessionId = waitForSessionId(requestId);
            PDAMod.LOGGER.debug("Received session ID {} for request {}", sessionId, requestId);
            return new DefaultSession(sessionId, context);
        }, PDAMod.EXECUTOR_SERVICE);
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
