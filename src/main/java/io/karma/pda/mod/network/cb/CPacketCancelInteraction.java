/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.network.cb;

import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 28/04/2024
 */
public final class CPacketCancelInteraction {
    private final UUID senderId;

    public CPacketCancelInteraction(final UUID senderId) {
        this.senderId = senderId;
    }

    public static void encode(final CPacketCancelInteraction packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.senderId);
    }

    public static CPacketCancelInteraction decode(final FriendlyByteBuf buffer) {
        return new CPacketCancelInteraction(buffer.readUUID());
    }
}
