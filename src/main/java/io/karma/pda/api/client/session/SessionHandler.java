/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.session;

import io.karma.pda.api.common.API;
import io.karma.pda.api.common.session.MuxedSession;
import io.karma.pda.api.common.session.Session;
import io.karma.pda.api.common.session.SessionContext;
import io.karma.pda.api.common.session.SelectiveSessionContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

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

    default <S> CompletableFuture<MuxedSession<S>> createSession(
        final Collection<? extends SelectiveSessionContext<S>> contexts, final S initial) {
        return CompletableFuture.supplyAsync(() -> {
            API.getLogger().debug("Creating multiplexed session asynchronously..");
            final var mux = new MuxedSession<>(initial, ConcurrentHashMap::new);
            // @formatter:off
            CompletableFuture.allOf(contexts.stream()
                .map(context -> createSession(context)
                    .thenAccept(session -> mux.addTarget(context.getSelector(), session)))
                .toArray(CompletableFuture[]::new)).join();
            // @formatter:on
            return mux;
        }, API.getExecutorService());
    }

    default void terminateSession() {
        final var session = getSession();
        if (session == null) {
            return;
        }
        terminateSession(session);
    }
}
