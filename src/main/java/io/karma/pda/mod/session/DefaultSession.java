/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.session;

import io.karma.pda.api.app.Launcher;
import io.karma.pda.api.session.Session;
import io.karma.pda.api.session.SessionContext;
import io.karma.pda.api.state.StateHandler;
import io.karma.pda.mod.app.DefaultLauncher;
import io.karma.pda.mod.state.DefaultStateHandler;

import java.time.Instant;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
public final class DefaultSession implements Session {
    private final DefaultStateHandler stateHandler = new DefaultStateHandler(this);
    private final DefaultLauncher launcher = new DefaultLauncher(this);
    private final UUID id;
    private final SessionContext context;
    private final Instant creationTime;

    DefaultSession(final UUID id, final SessionContext context) {
        this.id = id;
        this.context = context;
        creationTime = Instant.now();
    }

    @Override
    public SessionContext getContext() {
        return context;
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
    public Launcher getLauncher() {
        return launcher;
    }

    @Override
    public StateHandler getStateHandler() {
        return stateHandler;
    }
}
