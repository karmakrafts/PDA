/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.cb;

import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
public final class CPacketCreateSession {
    private final UUID requestId;
    private final UUID sessionId;

    public CPacketCreateSession(final UUID requestId, final UUID sessionId) {
        this.requestId = requestId;
        this.sessionId = sessionId;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public static void encode(final CPacketCreateSession packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.requestId);
        buffer.writeUUID(packet.sessionId);
    }

    public static CPacketCreateSession decode(final FriendlyByteBuf buffer) {
        final var requestId = buffer.readUUID();
        final var sessionId = buffer.readUUID();
        return new CPacketCreateSession(requestId, sessionId);
    }
}
