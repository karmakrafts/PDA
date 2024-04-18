/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.sb;

import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 18/04/2024
 */
public final class SPacketRemoveSyncValue {
    private final UUID sessionId;
    private final UUID id;

    public SPacketRemoveSyncValue(final UUID sessionId, final UUID id) {
        this.sessionId = sessionId;
        this.id = id;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public UUID getId() {
        return id;
    }

    public static void encode(final SPacketRemoveSyncValue packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.sessionId);
        buffer.writeUUID(packet.id);
    }

    public static SPacketRemoveSyncValue decode(final FriendlyByteBuf buffer) {
        final var sessionId = buffer.readUUID();
        final var id = buffer.readUUID();
        return new SPacketRemoveSyncValue(sessionId, id);
    }
}
