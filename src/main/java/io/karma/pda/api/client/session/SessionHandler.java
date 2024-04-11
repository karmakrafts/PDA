/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.session;

import io.karma.pda.api.common.API;
import io.karma.pda.api.common.session.MuxedSession;
import io.karma.pda.api.common.session.SelectiveSessionContext;
import io.karma.pda.api.common.session.Session;
import io.karma.pda.api.common.session.SessionContext;
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
            API.getLogger().debug("Creating multiplexed session asynchronously..");
            final var mux = new MuxedSession<>(initial, mapFactory);
            for (final var context : contexts) {
                try {
                    final var session = createSession(context).get();
                    mux.addTarget(context.getSelector(), session);
                    API.getLogger().debug("Added MUX target {}", session.getUUID());
                }
                catch (Throwable error) {
                    API.getLogger().error("Could not create MUX sub session");
                }
            }
            return mux;
        }, API.getExecutorService());
    }

    default <S> CompletableFuture<MuxedSession<S>> createSession(
        final Collection<? extends SelectiveSessionContext<S>> contexts, final S initial) {
        return createSession(HashMap::new, contexts, initial);
    }
}
