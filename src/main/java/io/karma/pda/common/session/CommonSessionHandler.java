/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.session;

import io.karma.pda.api.common.session.Session;
import io.karma.pda.api.common.session.SessionContext;
import io.karma.pda.common.PDAMod;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
public final class CommonSessionHandler {
    public static final CommonSessionHandler INSTANCE = new CommonSessionHandler();
    private final ConcurrentHashMap<UUID, Session> activeSessions = new ConcurrentHashMap<>();

    // @formatter:off
    private CommonSessionHandler() {}
    // @formatter:on

    public Session createSession(final SessionContext context) {
        final var sessionId = UUID.randomUUID();
        final var session = new DefaultSession(sessionId, context);
        activeSessions.put(sessionId, session);
        PDAMod.LOGGER.debug("Created session {}", sessionId);
        return session;
    }

    public void terminateSession(final UUID uuid) {
        activeSessions.remove(uuid);
        PDAMod.LOGGER.debug("Terminated session {}", uuid);
    }

    @ApiStatus.Internal
    public void setup() {

    }
}
