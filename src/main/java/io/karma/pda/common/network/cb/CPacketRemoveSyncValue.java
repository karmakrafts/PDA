/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.cb;

import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 18/04/2024
 */
public final class CPacketRemoveSyncValue {
    private final UUID sessionId;
    private final UUID playerId;
    private final UUID id;

    public CPacketRemoveSyncValue(final UUID sessionId, final @Nullable UUID playerId, final UUID id) {
        this.sessionId = sessionId;
        this.playerId = playerId;
        this.id = id;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public UUID getId() {
        return id;
    }

    public static void encode(final CPacketRemoveSyncValue packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.sessionId);
        final var playerId = packet.playerId;
        if (playerId != null) {
            buffer.writeBoolean(true);
            buffer.writeUUID(playerId);
        }
        else {
            buffer.writeBoolean(false);
        }
        buffer.writeUUID(packet.id);
    }

    public static CPacketRemoveSyncValue decode(final FriendlyByteBuf buffer) {
        final var sessionId = buffer.readUUID();
        final var playerId = buffer.readBoolean() ? buffer.readUUID() : null;
        final var id = buffer.readUUID();
        return new CPacketRemoveSyncValue(sessionId, playerId, id);
    }
}
