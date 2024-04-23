/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.session;

import io.karma.pda.api.common.session.MuxedSession;
import io.karma.pda.api.common.session.SelectiveSessionContext;
import io.karma.pda.api.common.session.Session;
import io.karma.pda.api.common.session.SessionContext;
import io.karma.pda.api.common.util.LogMarkers;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.network.sb.SPacketCreateSession;
import io.karma.pda.common.network.sb.SPacketTerminateSession;
import io.karma.pda.common.session.AbstractSessionHandler;
import io.karma.pda.common.util.BlockingHashMap;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
@OnlyIn(value = Dist.CLIENT)
public final class ClientSessionHandler extends AbstractSessionHandler {
    public static final ClientSessionHandler INSTANCE = new ClientSessionHandler();
    private final BlockingHashMap<UUID, UUID> pendingSessions = new BlockingHashMap<>();
    private final AtomicReference<Session> session = new AtomicReference<>(null);
    private final BlockingHashMap<UUID, Session> terminatedSessions = new BlockingHashMap<>();

    // @formatter:off
    private ClientSessionHandler() {}
    // @formatter:on

    @ApiStatus.Internal
    public void addTerminatedSession(final UUID sessionId) {
        if (terminatedSessions.containsKey(sessionId)) {
            PDAMod.LOGGER.warn(LogMarkers.PROTOCOL, "Session {} already terminated, ignoring", sessionId);
            return;
        }
        final var session = activeSessions.get(sessionId);
        if (session == null) {
            PDAMod.LOGGER.warn(LogMarkers.PROTOCOL, "Session {} does not exist, ignoring", sessionId);
            return;
        }
        terminatedSessions.put(sessionId, session);
        PDAMod.LOGGER.debug(LogMarkers.PROTOCOL, "Added terminated session {}", sessionId);
    }

    @ApiStatus.Internal
    public void addPendingSession(final UUID requestId, final UUID sessionId) {
        if (pendingSessions.containsKey(requestId)) {
            PDAMod.LOGGER.warn(LogMarkers.PROTOCOL, "Session {} is already pending, ignoring", sessionId);
            return;
        }
        pendingSessions.put(requestId, sessionId);
        PDAMod.LOGGER.debug(LogMarkers.PROTOCOL, "Added pending session {}", sessionId);
    }

    @Override
    public CompletableFuture<Session> createSession(final SessionContext context) {
        final var requestId = UUID.randomUUID();
        // @formatter:off
        final var future = pendingSessions.removeLater(requestId, 30, TimeUnit.SECONDS, PDAMod.EXECUTOR_SERVICE)
            .thenApply(sessionId -> {
                if (sessionId == null) {
                    PDAMod.LOGGER.error(LogMarkers.PROTOCOL, "Server didn't send session ID back in time for request {}, ignoring", requestId);
                    return null;
                }
                PDAMod.LOGGER.debug(LogMarkers.PROTOCOL, "Received session ID {} from server", sessionId);
                final var session = new ClientSession(sessionId, context);
                addActiveSession(sessionId, session);
                session.onEstablished();
                return (Session) session;
            });
        // @formatter:on
        Minecraft.getInstance().execute(() -> {
            PDAMod.LOGGER.debug(LogMarkers.PROTOCOL, "Requesting new session from server");
            PDAMod.CHANNEL.sendToServer(SPacketCreateSession.fromContext(requestId, context));
        });
        return future;
    }

    @Override
    public <S> CompletableFuture<MuxedSession<S>> createSession(
        final Collection<? extends SelectiveSessionContext<S>> contexts, final S initial) {
        PDAMod.LOGGER.debug(LogMarkers.PROTOCOL, "Requesting muxed session with {} contexts", contexts.size());
        return CompletableFuture.supplyAsync(() -> {
            final var mux = new MuxedSession<>(initial, ConcurrentHashMap::new);
            // @formatter:off
            CompletableFuture.allOf(contexts.stream()
                .map(context -> createSession(context)
                    .thenAccept(session -> mux.addTarget(context.getSelector(), session)))
                .toArray(CompletableFuture[]::new)).join();
            // @formatter:on
            PDAMod.LOGGER.debug(LogMarkers.PROTOCOL,
                "Created session multiplexer with {} sessions",
                mux.getTargets().size());
            return mux;
        }, PDAMod.EXECUTOR_SERVICE);
    }

    @Override
    public CompletableFuture<Void> terminateSession(final Session session) {
        // If this is a multiplexed session, unwind its targets and create a new future from each termination..
        // @formatter:off
        if (session instanceof MuxedSession<?> muxedSession) {
            return CompletableFuture.allOf(muxedSession.getTargets().stream()
                .map(this::terminateSession)
                .toArray(CompletableFuture[]::new));
        }
        final var sessionId = session.getId();
        final var future = terminatedSessions.removeLater(sessionId, 30, TimeUnit.SECONDS, PDAMod.EXECUTOR_SERVICE)
            .thenAccept(sess -> {
                if (sess == null) {
                    PDAMod.LOGGER.warn(LogMarkers.PROTOCOL, "Server didn't send acknowledgement back in time, ignoring");
                    return;
                }
                sess.onTerminated();
                removeActiveSession(sessionId);
            });
        // @formatter:on
        // ..otherwise this is a single-ended session, so we send a packet and wait for acknowledgement
        Minecraft.getInstance().execute(() -> {
            PDAMod.LOGGER.debug(LogMarkers.PROTOCOL, "Requesting termination for session {}", sessionId);
            PDAMod.CHANNEL.sendToServer(new SPacketTerminateSession(sessionId));
        });
        return future;
    }

    @Override
    public void setActiveSession(final @Nullable Session session) {
        PDAMod.LOGGER.debug(LogMarkers.PROTOCOL,
            "Setting active session to {}",
            session != null ? session.getId().toString() : "null");
        this.session.set(session);
    }

    @Nullable
    @Override
    public Session getActiveSession() {
        return session.get();
    }
}
