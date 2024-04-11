/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.session;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
public final class MuxedSession<S> implements Session {
    private final Map<S, Session> sessions;
    private S selector;

    public MuxedSession(final S initial, final Supplier<? extends Map<S, Session>> factory) {
        selector = initial;
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
        return sessions.get(selector);
    }

    public S getSelector() {
        return selector;
    }

    public void setSelector(final S selector) {
        this.selector = selector;
    }

    public Collection<Session> getTargets() {
        return sessions.values();
    }

    @Override
    public UUID getUUID() {
        return getTarget().getUUID();
    }

    @Override
    public SessionContext getContext() {
        return getTarget().getContext();
    }
}
