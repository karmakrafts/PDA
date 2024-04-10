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
public final class SessionDataHandler {
    public static final SessionDataHandler INSTANCE = new SessionDataHandler();
    private final HashMap<UUID, Session> activeSessions = new HashMap<>();

    // @formatter:off
    private SessionDataHandler() {}
    // @formatter:on

    public void createSession(final UUID uuid, final SessionContext context) {
        if (activeSessions.containsKey(uuid)) {
            return;
        }
        activeSessions.put(uuid, new DefaultSession(uuid, context));
        PDAMod.LOGGER.debug("Created session {} on SERVER", uuid);
    }

    public void terminateSession(final UUID uuid) {
        activeSessions.remove(uuid);
        PDAMod.LOGGER.debug("Terminated session {} on SERVER", uuid);
    }

    @ApiStatus.Internal
    public void setup() {

    }
}
