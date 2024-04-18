/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.cb;

import io.karma.pda.api.common.util.JSONUtils;
import io.karma.pda.common.PDAMod;
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
    private final Object initialValue;

    public CPacketAddSyncValue(final UUID sessionId, final @Nullable UUID playerId, final UUID oldId, final UUID newId,
                               final @Nullable Object initialValue) {
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

    public @Nullable Object getInitialValue() {
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
        final var initialValue = packet.initialValue;
        if (initialValue != null) {
            buffer.writeBoolean(true);
            buffer.writeUtf(initialValue.getClass().getName());
            buffer.writeByteArray(JSONUtils.compress(initialValue));
        }
        else {
            buffer.writeBoolean(false);
        }
    }

    public static CPacketAddSyncValue decode(final FriendlyByteBuf buffer) {
        final var sessionId = buffer.readUUID();
        final var playerId = buffer.readBoolean() ? buffer.readUUID() : null;
        final var oldId = buffer.readUUID();
        final var newId = buffer.readUUID();
        if (buffer.readBoolean()) {
            try {
                final var type = Class.forName(buffer.readUtf());
                final var initialValue = JSONUtils.decompress(buffer.readByteArray(), type);
                return new CPacketAddSyncValue(sessionId, playerId, oldId, newId, initialValue);
            }
            catch (Throwable error) {
                PDAMod.LOGGER.error("Could not decode add sync value packet: {}", error.getMessage());
            }
        }
        return new CPacketAddSyncValue(sessionId, playerId, oldId, newId, null);
    }
}
