/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.session;

import io.karma.pda.api.common.session.*;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.network.sb.SPacketCreateSession;
import io.karma.pda.common.network.sb.SPacketTerminateSession;
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
public final class ClientSessionHandler implements SessionHandler {
    public static final ClientSessionHandler INSTANCE = new ClientSessionHandler();
    private final BlockingHashMap<UUID, UUID> pendingSessions = new BlockingHashMap<>();
    private final AtomicReference<Session> session = new AtomicReference<>(null);
    private final ConcurrentHashMap<UUID, Session> activeSessions = new ConcurrentHashMap<>();
    private final BlockingHashMap<UUID, Session> terminatedSessions = new BlockingHashMap<>();

    // @formatter:off
    private ClientSessionHandler() {}
    // @formatter:on

    @ApiStatus.Internal
    public void addActiveSession(final UUID sessionId, final Session session) {
        if (activeSessions.containsKey(sessionId)) {
            PDAMod.LOGGER.warn("Session {} already exists, ignoring", sessionId);
            return;
        }
        activeSessions.put(sessionId, session);
    }

    @ApiStatus.Internal
    public void removeActiveSession(final UUID sessionId) {
        activeSessions.remove(sessionId);
    }

    @ApiStatus.Internal
    public void addTerminatedSession(final UUID sessionId) {
        if (terminatedSessions.containsKey(sessionId)) {
            PDAMod.LOGGER.warn("Session {} already terminated, ignoring", sessionId);
            return;
        }
        final var session = activeSessions.get(sessionId);
        if (session == null) {
            PDAMod.LOGGER.warn("Session {} does not exist, ignoring", sessionId);
            return;
        }
        terminatedSessions.put(sessionId, session);
    }

    @ApiStatus.Internal
    public void addPendingSession(final UUID requestId, final UUID sessionId) {
        if (pendingSessions.containsKey(requestId)) {
            PDAMod.LOGGER.warn("Session {} is already pending, ignoring", sessionId);
            return;
        }
        pendingSessions.put(requestId, sessionId);
    }

    @Override
    public CompletableFuture<Session> createSession(final SessionContext context) {
        final var requestId = UUID.randomUUID();
        Minecraft.getInstance().execute(() -> {
            PDAMod.LOGGER.debug("Requesting new session from server");
            PDAMod.CHANNEL.sendToServer(SPacketCreateSession.fromContext(requestId, context));
        });
        // @formatter:off
        return pendingSessions.removeLater(requestId, 200, TimeUnit.MILLISECONDS, PDAMod.EXECUTOR_SERVICE)
            .thenApply(sessionId -> {
                if (sessionId == null) {
                    PDAMod.LOGGER.error("Server didn't send session ID back in time for request {}, ignoring",
                        requestId);
                    return null;
                }
                PDAMod.LOGGER.debug("Received session ID {} from server", sessionId);
                final var session = new ClientSession(sessionId, context);
                addActiveSession(sessionId, session);
                return session;
            });
        // @formatter:on
    }

    @Override
    public <S> CompletableFuture<MuxedSession<S>> createSession(
        final Collection<? extends SelectiveSessionContext<S>> contexts, final S initial) {
        PDAMod.LOGGER.debug("Requesting muxed session with {} contexts", contexts.size());
        return CompletableFuture.supplyAsync(() -> {
            final var mux = new MuxedSession<>(initial, ConcurrentHashMap::new);
            CompletableFuture.allOf(contexts.stream().map(context -> createSession(context).thenApply(session -> {
                mux.addTarget(context.getSelector(), session);
                return null;
            })).toArray(CompletableFuture[]::new)).join();
            PDAMod.LOGGER.debug("Created session multiplexer with {} sessions", mux.getTargets().size());
            return mux;
        }, PDAMod.EXECUTOR_SERVICE);
    }

    @Override
    public CompletableFuture<Void> terminateSession(final Session session) {
        // If this is a multiplexed session, unwind its targets and create a new future from each termination..
        if (session instanceof MuxedSession<?> muxedSession) { // @formatter:off
            return CompletableFuture.allOf(muxedSession.getTargets().stream()
                .map(this::terminateSession)
                .toArray(CompletableFuture[]::new));
        } // @formatter:on
        // ..otherwise this is a single-ended session, so we send a packet and wait for acknowledgement
        Minecraft.getInstance().execute(() -> {
            final var id = session.getId();
            PDAMod.LOGGER.debug("Requesting termination for session {}", id);
            PDAMod.CHANNEL.sendToServer(new SPacketTerminateSession(id));
        });
        // @formatter:off
        return terminatedSessions.removeLater(session.getId(), 200, TimeUnit.MILLISECONDS, PDAMod.EXECUTOR_SERVICE)
            .thenApply(sess -> {
                if(sess == null) {
                    PDAMod.LOGGER.warn("Server didn't send acknowledgement back in time, ignoring");
                    return null;
                }
                removeActiveSession(sess.getId());
                return null;
            });
        // @formatter:on
    }

    @Nullable
    @Override
    public Session getActiveSession(final UUID id) {
        return activeSessions.get(id);
    }

    @Override
    public void setActiveSession(final @Nullable Session session) {
        PDAMod.LOGGER.debug("Setting active session to {}", session != null ? session.getId().toString() : "null");
        this.session.set(session);
    }

    @Nullable
    @Override
    public Session getActiveSession() {
        return session.get();
    }
}
