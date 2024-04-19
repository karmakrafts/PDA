/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.cb;

import io.karma.pda.api.common.util.JSONUtils;
import io.karma.pda.api.common.util.TypedValue;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 18/04/2024
 */
public final class CPacketAddSyncValue {
    private final UUID sessionId;
    private final UUID playerId;
    private final UUID oldId;
    private final UUID newId;
    private final TypedValue<?> initialValue;

    public CPacketAddSyncValue(final UUID sessionId, final @Nullable UUID playerId, final UUID oldId, final UUID newId,
                               final TypedValue<?> initialValue) {
        this.sessionId = sessionId;
        this.playerId = playerId;
        this.oldId = oldId;
        this.newId = newId;
        this.initialValue = initialValue;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public @Nullable UUID getPlayerId() {
        return playerId;
    }

    public UUID getOldId() {
        return oldId;
    }

    public UUID getNewId() {
        return newId;
    }

    public TypedValue<?> getInitialValue() {
        return initialValue;
    }

    public static void encode(final CPacketAddSyncValue packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.sessionId);
        final var playerId = packet.playerId;
        if (playerId != null) {
            buffer.writeBoolean(true);
            buffer.writeUUID(playerId);
        }
        else {
            buffer.writeBoolean(false);
        }
        buffer.writeUUID(packet.oldId);
        buffer.writeUUID(packet.newId);
        buffer.writeByteArray(JSONUtils.compress(packet.initialValue.get()));
    }

    public static CPacketAddSyncValue decode(final FriendlyByteBuf buffer) {
        final var sessionId = buffer.readUUID();
        final var playerId = buffer.readBoolean() ? buffer.readUUID() : null;
        final var oldId = buffer.readUUID();
        final var newId = buffer.readUUID();
        final var initialValue = TypedValue.fromPair(JSONUtils.decompress(buffer.readByteArray()));
        return new CPacketAddSyncValue(sessionId, playerId, oldId, newId, initialValue);
    }
}
