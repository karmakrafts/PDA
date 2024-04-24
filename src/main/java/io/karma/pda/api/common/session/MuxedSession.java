/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.session;

import io.karma.pda.api.common.app.Launcher;
import io.karma.pda.api.common.sync.Synchronizer;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
public final class MuxedSession<S> implements Session {
    private final Map<S, Session> sessions;
    private final AtomicReference<S> selector = new AtomicReference<>();

    public MuxedSession(final S initial, final Supplier<? extends Map<S, Session>> factory) {
        selector.set(initial);
        sessions = factory.get();
    }

    public void addTarget(final S selector, final Session session) {
        if (sessions.containsKey(selector)) {
            throw new IllegalArgumentException("Session already exists");
        }
        sessions.put(selector, session);
    }

    public void removeTarget(final S selector) {
        if (!sessions.containsKey(selector)) {
            throw new IllegalArgumentException("Session does not exist");
        }
        sessions.remove(selector);
    }

    public Session getTarget() {
        return sessions.get(selector.get());
    }

    public S getSelector() {
        return selector.get();
    }

    public void setSelector(final S selector) {
        this.selector.set(selector);
    }

    public Collection<Session> getTargets() {
        return sessions.values();
    }

    public Set<S> getSelectors() {
        return sessions.keySet();
    }

    @Override
    public Launcher getLauncher() {
        return getTarget().getLauncher();
    }

    @Override
    public UUID getId() {
        return getTarget().getId();
    }

    @Override
    public SessionContext getContext() {
        return getTarget().getContext();
    }

    @Override
    public Synchronizer getSynchronizer() {
        return getTarget().getSynchronizer();
    }
}
