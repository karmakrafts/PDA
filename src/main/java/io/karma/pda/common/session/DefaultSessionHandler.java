/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.session;

import io.karma.pda.api.common.session.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
public final class DefaultSessionHandler implements SessionHandler {
    public static final DefaultSessionHandler INSTANCE = new DefaultSessionHandler();
    private final ConcurrentHashMap<UUID, Session> activeSessions = new ConcurrentHashMap<>();

    // @formatter:off
    private DefaultSessionHandler() {}
    // @formatter:on

    @Override
    public CompletableFuture<Session> createSession(final SessionContext context) {
        final var id = UUID.randomUUID();
        final var session = new DefaultSession(id, context);
        activeSessions.put(id, session);
        return CompletableFuture.completedFuture(session);
    }

    @Override
    public <S> CompletableFuture<MuxedSession<S>> createSession(
        Collection<? extends SelectiveSessionContext<S>> selectiveSessionContexts, S initial) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void terminateSession(final Session session) {
        final var id = session.getId();
        if (!activeSessions.containsKey(id)) {
            return;
        }
        session.onTermination();
        activeSessions.remove(id);
    }

    @Override
    public void setActiveSession(final @Nullable Session session) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public Session getActiveSession() {
        throw new UnsupportedOperationException();
    }

    @ApiStatus.Internal
    public void setup() {

    }

    public @Nullable Session getActiveSession(final UUID id) {
        return activeSessions.get(id);
    }
}
