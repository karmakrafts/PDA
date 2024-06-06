/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.network.sb;

import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 06/04/2024
 */
public final class SPacketTerminateSession {
    private final UUID id;

    public SPacketTerminateSession(final UUID id) {
        this.id = id;
    }

    public static void encode(final SPacketTerminateSession packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.id);
    }

    public static SPacketTerminateSession decode(final FriendlyByteBuf buffer) {
        final var id = buffer.readUUID();
        return new SPacketTerminateSession(id);
    }

    public UUID getId() {
        return id;
    }
}
