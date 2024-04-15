/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.sb;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 14/04/2024
 */
public final class SPacketOpenApp {
    private final UUID sessionId;
    private final ResourceLocation name;

    public SPacketOpenApp(final UUID sessionId, final ResourceLocation name) {
        this.sessionId = sessionId;
        this.name = name;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public ResourceLocation getName() {
        return name;
    }

    public static void encode(final SPacketOpenApp packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.sessionId);
        buffer.writeResourceLocation(packet.name);
    }

    public static SPacketOpenApp decode(final FriendlyByteBuf buffer) {
        final var sessionId = buffer.readUUID();
        final var name = buffer.readResourceLocation();
        return new SPacketOpenApp(sessionId, name);
    }
}
