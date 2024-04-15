/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.session;

import io.karma.pda.api.common.session.MuxedSession;
import io.karma.pda.api.common.session.SelectiveSessionContext;
import io.karma.pda.api.common.session.Session;
import io.karma.pda.api.common.session.SessionContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
@OnlyIn(Dist.CLIENT)
public interface SessionHandler {
    CompletableFuture<Session> createSession(final SessionContext context);

    <S> CompletableFuture<MuxedSession<S>> createSession(
        final Collection<? extends SelectiveSessionContext<S>> contexts, final S initial);

    void terminateSession(final Session session);

    void setActiveSession(final @Nullable Session session);

    @Nullable
    Session getActiveSession();
}
