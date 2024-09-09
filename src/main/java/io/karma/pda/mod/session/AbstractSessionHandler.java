/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.session;

import io.karma.pda.api.session.Session;
import io.karma.pda.api.session.SessionHandler;
import io.karma.pda.mod.PDAMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import java.util.*;
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
    public @Nullable Session findByPosition(final Level level, final BlockPos pos) {
        final var sessions = activeSessions.values();
        final var currentDimension = level.dimension();
        for (final var session : sessions) {
            final var context = session.getContext();
            if (context.getType().isHandheld()) {
                continue;
            }
            final var dimension = context.getLevel().dimension();
            if (!dimension.equals(currentDimension) || !Objects.requireNonNull(context.getPos()).equals(pos)) {
                continue;
            }
            return session;
        }
        return null;
    }

    @Override
    public @Nullable Session findByDevice(final ItemStack stack) {
        final var sessions = activeSessions.values();
        for (final var session : sessions) {
            final var context = session.getContext();
            if (context.getDeviceItem() != stack) {
                continue;
            }
            return session;
        }
        return null;
    }

    @Internal
    public void addActiveSession(final UUID sessionId, final Session session) {
        if (activeSessions.containsKey(sessionId)) {
            PDAMod.LOGGER.warn("Session {} already exists, ignoring", sessionId);
            return;
        }
        activeSessions.put(sessionId, session);
        PDAMod.LOGGER.debug("Added active session {}", sessionId);
    }

    @Internal
    public void removeActiveSession(final UUID sessionId) {
        activeSessions.remove(sessionId);
        PDAMod.LOGGER.debug("Removed active session {}", sessionId);
    }

    @Internal
    public Map<UUID, Session> getActiveSessions() {
        return Collections.unmodifiableMap(activeSessions);
    }
}
