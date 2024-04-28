/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.cb;

import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 17/04/2024
 */
public final class CPacketTerminateSession {
    private final UUID sessionId;
    private final UUID playerId;

    public CPacketTerminateSession(final UUID sessionId, final UUID playerId) {
        this.sessionId = sessionId;
        this.playerId = playerId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public static void encode(final CPacketTerminateSession packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.sessionId);
        buffer.writeUUID(packet.playerId);
    }

    public static CPacketTerminateSession decode(final FriendlyByteBuf buffer) {
        final var sessionId = buffer.readUUID();
        final var playerId = buffer.readUUID();
        return new CPacketTerminateSession(sessionId, playerId);
    }
}
