/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.sb;

import io.karma.pda.api.common.util.JSONUtils;
import io.karma.pda.common.PDAMod;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 18/04/2024
 */
public final class SPacketAddSyncValue {
    private final UUID sessionId;
    private final UUID initialId;
    private final Object initialValue;

    public SPacketAddSyncValue(final UUID sessionId, final UUID initialId, final @Nullable Object initialValue) {
        this.sessionId = sessionId;
        this.initialId = initialId;
        this.initialValue = initialValue;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public UUID getInitialId() {
        return initialId;
    }

    public @Nullable Object getInitialValue() {
        return initialValue;
    }

    public static void encode(final SPacketAddSyncValue packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.sessionId);
        buffer.writeUUID(packet.initialId);
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

    public static SPacketAddSyncValue decode(final FriendlyByteBuf buffer) {
        final var sessionId = buffer.readUUID();
        final var initialId = buffer.readUUID();
        final var hasInitialValue = buffer.readBoolean();
        if (hasInitialValue) {
            try {
                final var type = Class.forName(buffer.readUtf());
                final var initialValue = JSONUtils.decompress(buffer.readByteArray(), type);
                return new SPacketAddSyncValue(sessionId, initialId, initialValue);
            }
            catch (Throwable error) {
                PDAMod.LOGGER.error("Could not decode add sync value packet: {}", error.getMessage());
            }
        }
        return new SPacketAddSyncValue(sessionId, initialId, null);
    }
}
