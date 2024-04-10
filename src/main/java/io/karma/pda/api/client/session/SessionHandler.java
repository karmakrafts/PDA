/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.session;

import io.karma.pda.api.common.API;
import io.karma.pda.api.common.session.MuxedSession;
import io.karma.pda.api.common.session.SelectiveSessionContext;
import io.karma.pda.api.common.session.Session;
import io.karma.pda.api.common.session.SessionContext;
import io.karma.pda.common.PDAMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
@OnlyIn(Dist.CLIENT)
public interface SessionHandler {
    CompletableFuture<Session> createSession(final SessionContext context);

    void terminateSession(final Session session);

    @Nullable
    Session getSession();

    void setSession(final Session session);

    default <S> CompletableFuture<MuxedSession<S>> createSession(final Supplier<Map<S, Session>> mapFactory,
                                                                 final Collection<? extends SelectiveSessionContext<S>> contexts,
                                                                 final S initial) {
        return CompletableFuture.supplyAsync(() -> {
            final var session = new MuxedSession<>(initial, mapFactory);
            for (final var context : contexts) {
                try {
                    session.addTarget(context.getSelector(), createSession(context).get());
                }
                catch (Throwable error) {
                    PDAMod.LOGGER.error("Could not create MUX sub session");
                }
            }
            return session;
        }, API.getExecutorService());
    }

    default <S> CompletableFuture<MuxedSession<S>> createSession(
        final Collection<? extends SelectiveSessionContext<S>> contexts, final S initial) {
        return createSession(HashMap::new, contexts, initial);
    }
}
