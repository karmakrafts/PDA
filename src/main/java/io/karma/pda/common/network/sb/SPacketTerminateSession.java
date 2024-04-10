/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.sb;

import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 06/04/2024
 */
public final class SPacketTerminateSession {
    private final UUID uuid;

    public SPacketTerminateSession(final UUID uuid) {
        this.uuid = uuid;
    }

    public static void encode(final SPacketTerminateSession packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.uuid);
    }

    public static SPacketTerminateSession decode(final FriendlyByteBuf buffer) {
        final var uuid = buffer.readUUID();
        return new SPacketTerminateSession(uuid);
    }

    public UUID getUUID() {
        return uuid;
    }
}
