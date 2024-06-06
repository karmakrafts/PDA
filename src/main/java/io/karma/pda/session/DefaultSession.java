/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.session;

import io.karma.pda.api.app.Launcher;
import io.karma.pda.api.session.Session;
import io.karma.pda.api.session.SessionContext;
import io.karma.pda.api.state.StateHandler;
import io.karma.pda.app.DefaultLauncher;
import io.karma.pda.state.DefaultStateHandler;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Function;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
public class DefaultSession implements Session {
    protected final UUID id;
    protected final SessionContext context;
    protected final StateHandler stateHandler;
    protected final Launcher launcher;
    protected final Instant creationTime;

    public DefaultSession(final UUID id, final SessionContext context,
                          final Function<Session, StateHandler> synchronizerFactory,
                          final Function<Session, Launcher> launcherFactory) {
        this.id = id;
        this.context = context;
        stateHandler = synchronizerFactory.apply(this);
        launcher = launcherFactory.apply(this);
        creationTime = Instant.now();
    }

    public DefaultSession(final UUID id, final SessionContext context) {
        this(id, context, DefaultStateHandler::new, DefaultLauncher::new);
    }

    @Override
    public Instant getCreationTime() {
        return creationTime;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public SessionContext getContext() {
        return context;
    }

    @Override
    public Launcher getLauncher() {
        return launcher;
    }

    @Override
    public StateHandler getStateHandler() {
        return stateHandler;
    }
}
