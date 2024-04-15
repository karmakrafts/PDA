/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.session;

import io.karma.pda.api.client.session.SessionHandler;
import io.karma.pda.api.common.session.MuxedSession;
import io.karma.pda.api.common.session.SelectiveSessionContext;
import io.karma.pda.api.common.session.Session;
import io.karma.pda.api.common.session.SessionContext;
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
    private final BlockingHashMap<UUID, UUID> newlyCreatedSessions = new BlockingHashMap<>();
    private final AtomicReference<Session> session = new AtomicReference<>(null);

    // @formatter:off
    private ClientSessionHandler() {}
    // @formatter:on

    @ApiStatus.Internal
    public void addNewSessionId(final UUID requestId, final UUID sessionId) {
        if (newlyCreatedSessions.containsKey(requestId)) {
            return;
        }
        newlyCreatedSessions.put(requestId, sessionId);
    }

    private CompletableFuture<UUID> getSessionId(final UUID requestId) {
        return newlyCreatedSessions.removeLater(requestId, 200, TimeUnit.MILLISECONDS, PDAMod.EXECUTOR_SERVICE);
    }

    @Override
    public CompletableFuture<Session> createSession(final SessionContext context) {
        final var requestId = UUID.randomUUID();
        Minecraft.getInstance().execute(() -> {
            PDAMod.LOGGER.debug("Requesting new session from server");
            PDAMod.CHANNEL.sendToServer(SPacketCreateSession.fromContext(requestId, context));
        });
        return getSessionId(requestId).thenApply(sessionId -> {
            PDAMod.LOGGER.debug("Received session ID {} from server", sessionId);
            return new ClientSession(sessionId, context);
        });
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
    public void terminateSession(final Session session) {
        final var game = Minecraft.getInstance();
        game.execute(() -> {
            if (session instanceof MuxedSession<?> muxedSession) {
                for (final var target : muxedSession.getTargets()) {
                    final var id = target.getId();
                    PDAMod.LOGGER.debug("Requesting termination for session {}", id);
                    PDAMod.CHANNEL.sendToServer(new SPacketTerminateSession(id));
                }
                return;
            }
            final var id = session.getId();
            PDAMod.LOGGER.debug("Requesting termination for session {}", id);
            PDAMod.CHANNEL.sendToServer(new SPacketTerminateSession(id));
            session.onTermination();
        });
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
