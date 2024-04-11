/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.session;

import io.karma.pda.api.common.session.Session;
import io.karma.pda.api.common.session.SessionContext;
import io.karma.pda.common.PDAMod;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
public final class ServerSessionHandler {
    public static final ServerSessionHandler INSTANCE = new ServerSessionHandler();
    private final HashMap<UUID, Session> activeSessions = new HashMap<>();

    // @formatter:off
    private ServerSessionHandler() {}
    // @formatter:on

    public Session createSession(final SessionContext context) {
        final var uuid = UUID.randomUUID();
        final var session = new DefaultSession(uuid, context);
        activeSessions.put(uuid, session);
        PDAMod.LOGGER.debug("Created session {}", uuid);
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
