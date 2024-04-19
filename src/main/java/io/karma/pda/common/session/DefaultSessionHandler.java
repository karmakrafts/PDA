/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.session;

import io.karma.pda.api.common.session.MuxedSession;
import io.karma.pda.api.common.session.SelectiveSessionContext;
import io.karma.pda.api.common.session.Session;
import io.karma.pda.api.common.session.SessionContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
public class DefaultSessionHandler extends AbstractSessionHandler {
    public static final DefaultSessionHandler INSTANCE = new DefaultSessionHandler();

    // @formatter:off
    protected DefaultSessionHandler() {}
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
    public CompletableFuture<Void> terminateSession(final Session session) {
        final var id = session.getId();
        if (!activeSessions.containsKey(id)) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Session not found"));
        }
        session.onTermination();
        activeSessions.remove(id);
        return CompletableFuture.completedFuture(null);
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
}
