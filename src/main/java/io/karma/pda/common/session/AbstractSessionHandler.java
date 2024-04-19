/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.session;

import io.karma.pda.api.common.session.Session;
import io.karma.pda.api.common.session.SessionHandler;
import io.karma.pda.api.common.util.LogMarkers;
import io.karma.pda.common.PDAMod;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alexander Hinze
 * @since 19/04/2024
 */
public abstract class AbstractSessionHandler implements SessionHandler {
    protected final ConcurrentHashMap<UUID, Session> activeSessions = new ConcurrentHashMap<>();

    @Nullable
    @Override
    public Session findById(final UUID id) {
        return activeSessions.get(id);
    }

    @Nullable
    @Override
    public Session findByDevice(final ItemStack stack) {
        final var sessions = activeSessions.values();
        for (final var session : sessions) {
            if (session.getContext().getDeviceItem() != stack) {
                continue;
            }
            return session;
        }
        return null;
    }

    @ApiStatus.Internal
    public void addActiveSession(final UUID sessionId, final Session session) {
        if (activeSessions.containsKey(sessionId)) {
            PDAMod.LOGGER.warn(LogMarkers.PROTOCOL, "Session {} already exists, ignoring", sessionId);
            return;
        }
        activeSessions.put(sessionId, session);
        PDAMod.LOGGER.debug(LogMarkers.PROTOCOL, "Added active session {}", sessionId);
    }

    @ApiStatus.Internal
    public void removeActiveSession(final UUID sessionId) {
        activeSessions.remove(sessionId);
        PDAMod.LOGGER.debug(LogMarkers.PROTOCOL, "Removed active session {}", sessionId);
    }
}
