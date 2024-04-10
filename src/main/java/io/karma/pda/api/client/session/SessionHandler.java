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
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
@OnlyIn(Dist.CLIENT)
public interface SessionHandler {
    Session createSession(final SessionContext context);

    void terminateSession(final Session session);

    @Nullable
    Session getSession();

    void setSession(final Session session);

    default <S> MuxedSession<S> createSession(final Supplier<Map<S, Session>> mapFactory,
                                              final Collection<? extends SelectiveSessionContext<S>> contexts,
                                              final S initial) {
        final var session = new MuxedSession<>(initial, mapFactory);
        for (final var context : contexts) {
            session.addTarget(context.getSelector(), createSession(context));
        }
        return session;
    }

    default <S> MuxedSession<S> createSession(final Collection<? extends SelectiveSessionContext<S>> contexts,
                                              final S initial) {
        return createSession(HashMap::new, contexts, initial);
    }
}
