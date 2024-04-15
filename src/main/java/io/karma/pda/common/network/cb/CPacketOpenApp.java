/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.cb;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * @author Alexander Hinze
 * @since 14/04/2024
 */
public final class CPacketOpenApp {
    private final ResourceLocation name;

    public CPacketOpenApp(final ResourceLocation name) {
        this.name = name;
    }

    public ResourceLocation getName() {
        return name;
    }

    public static void encode(final CPacketOpenApp packet, final FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(packet.getName());
    }

    public static CPacketOpenApp decode(final FriendlyByteBuf buffer) {
        final ResourceLocation name = buffer.readResourceLocation();
        return new CPacketOpenApp(name);
    }
}
