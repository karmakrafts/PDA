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

    public CPacketTerminateSession(final UUID sessionId) {
        this.sessionId = sessionId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public static void encode(final CPacketTerminateSession packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.sessionId);
    }

    public static CPacketTerminateSession decode(final FriendlyByteBuf buffer) {
        final var sessionId = buffer.readUUID();
        return new CPacketTerminateSession(sessionId);
    }
}
