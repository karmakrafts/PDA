/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.cb;

import net.minecraft.network.FriendlyByteBuf;

/**
 * @author Alexander Hinze
 * @since 28/04/2024
 */
public final class CPacketCancelInteraction {
    public static void encode(final CPacketCancelInteraction packet, final FriendlyByteBuf buffer) {
    }

    public static CPacketCancelInteraction decode(final FriendlyByteBuf buffer) {
        return new CPacketCancelInteraction();
    }
}
