/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.cb;

import io.karma.pda.common.util.PacketUtils;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 17/04/2024
 */
public final class CPacketTerminateSession {
    private final UUID sessionId;
    private final UUID playerId;

    public CPacketTerminateSession(final UUID sessionId, final @Nullable UUID playerId) {
        this.sessionId = sessionId;
        this.playerId = playerId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public @Nullable UUID getPlayerId() {
        return playerId;
    }

    public static void encode(final CPacketTerminateSession packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.sessionId);
        PacketUtils.writeNullable(packet.playerId, FriendlyByteBuf::writeUUID, buffer);
    }

    public static CPacketTerminateSession decode(final FriendlyByteBuf buffer) {
        final var sessionId = buffer.readUUID();
        final var playerId = PacketUtils.readNullable(buffer, FriendlyByteBuf::readUUID);
        return new CPacketTerminateSession(sessionId, playerId);
    }
}
