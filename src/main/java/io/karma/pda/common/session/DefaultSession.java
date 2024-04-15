/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.session;

import io.karma.pda.api.common.app.Launcher;
import io.karma.pda.api.common.session.Session;
import io.karma.pda.api.common.session.SessionContext;
import io.karma.pda.common.app.DefaultLauncher;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
public class DefaultSession implements Session {
    protected final UUID id;
    protected final SessionContext context;

    public DefaultSession(final UUID id, final SessionContext context) {
        this.id = id;
        this.context = context;
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
        return DefaultLauncher.INSTANCE;
    }

    @Override
    public void onTermination() {

    }
}
