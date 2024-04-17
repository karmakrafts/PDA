/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.session;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
public interface SessionHandler {
    CompletableFuture<Session> createSession(final SessionContext context);

    <S> CompletableFuture<MuxedSession<S>> createSession(
        final Collection<? extends SelectiveSessionContext<S>> contexts, final S initial);

    CompletableFuture<Void> terminateSession(final Session session);

    @Nullable
    Session getActiveSession(final UUID id);

    default void setActiveSession(final @Nullable Session session) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    default Session getActiveSession() {
        throw new UnsupportedOperationException();
    }
}
