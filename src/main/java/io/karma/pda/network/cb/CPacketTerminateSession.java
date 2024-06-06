/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.network.cb;

import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 17/04/2024
 */
public final class CPacketTerminateSession {
    private final UUID sessionId;
    private final UUID playerId;
    private final boolean isPending;

    public CPacketTerminateSession(final UUID sessionId, final UUID playerId, final boolean isPending) {
        this.sessionId = sessionId;
        this.playerId = playerId;
        this.isPending = isPending;
    }

    public static void encode(final CPacketTerminateSession packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.sessionId);
        buffer.writeUUID(packet.playerId);
        buffer.writeBoolean(packet.isPending);
    }

    public static CPacketTerminateSession decode(final FriendlyByteBuf buffer) {
        final var sessionId = buffer.readUUID();
        final var playerId = buffer.readUUID();
        final var isPending = buffer.readBoolean();
        return new CPacketTerminateSession(sessionId, playerId, isPending);
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public boolean isPending() {
        return isPending;
    }
}
