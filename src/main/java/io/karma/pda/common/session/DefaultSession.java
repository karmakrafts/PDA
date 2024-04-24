/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.session;

import io.karma.pda.api.common.app.Launcher;
import io.karma.pda.api.common.session.Session;
import io.karma.pda.api.common.session.SessionContext;
import io.karma.pda.api.common.sync.Synchronizer;
import io.karma.pda.common.app.DefaultLauncher;
import io.karma.pda.common.sync.DefaultSynchronizer;

import java.util.UUID;
import java.util.function.Function;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
public class DefaultSession implements Session {
    protected final UUID id;
    protected final SessionContext context;
    protected final Synchronizer synchronizer;
    protected final Launcher launcher;

    public DefaultSession(final UUID id, final SessionContext context,
                          final Function<Session, Synchronizer> synchronizerFactory,
                          final Function<Session, Launcher> launcherFactory) {
        this.id = id;
        this.context = context;
        synchronizer = synchronizerFactory.apply(this);
        launcher = launcherFactory.apply(this);
    }

    public DefaultSession(final UUID id, final SessionContext context) {
        this(id, context, DefaultSynchronizer::new, DefaultLauncher::new);
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
    public Synchronizer getSynchronizer() {
        return synchronizer;
    }
}
