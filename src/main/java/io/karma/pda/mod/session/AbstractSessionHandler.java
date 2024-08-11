/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.session;

import io.karma.pda.api.session.Session;
import io.karma.pda.api.session.SessionHandler;
import io.karma.pda.api.util.LogMarkers;
import io.karma.pda.mod.PDAMod;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alexander Hinze
 * @since 19/04/2024
 */
public abstract class AbstractSessionHandler implements SessionHandler {
    protected final ConcurrentHashMap<UUID, Session> activeSessions = new ConcurrentHashMap<>();

    @Override
    public List<Session> findByPlayer(final Player player) {
        // @formatter:off
        return activeSessions.values()
            .stream()
            .filter(s -> s.getContext().getPlayer().getUUID().equals(player.getUUID()))
            .toList();
        // @formatter:on
    }

    @Override
    public @Nullable Session findById(final UUID id) {
        return activeSessions.get(id);
    }

    @Override
    public @Nullable Session findByDevice(final ItemStack stack) {
        final var sessions = activeSessions.values();
        for (final var session : sessions) {
            if (session.getContext().getDeviceItem() != stack) {
                continue;
            }
            return session;
        }
        return null;
    }

    @Internal
    public void addActiveSession(final UUID sessionId, final Session session) {
        if (activeSessions.containsKey(sessionId)) {
            PDAMod.LOGGER.warn(LogMarkers.PROTOCOL, "Session {} already exists, ignoring", sessionId);
            return;
        }
        activeSessions.put(sessionId, session);
        PDAMod.LOGGER.debug(LogMarkers.PROTOCOL, "Added active session {}", sessionId);
    }

    @Internal
    public void removeActiveSession(final UUID sessionId) {
        activeSessions.remove(sessionId);
        PDAMod.LOGGER.debug(LogMarkers.PROTOCOL, "Removed active session {}", sessionId);
    }

    @Internal
    public Map<UUID, Session> getActiveSessions() {
        return Collections.unmodifiableMap(activeSessions);
    }
}
