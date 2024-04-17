/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.cb;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 17/04/2024
 */
public final class CPacketCloseApp {
    private final UUID sessionId;
    private final ResourceLocation name;

    public CPacketCloseApp(final UUID sessionId, final ResourceLocation name) {
        this.sessionId = sessionId;
        this.name = name;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public ResourceLocation getName() {
        return name;
    }

    public static void encode(final CPacketCloseApp packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.sessionId);
        buffer.writeResourceLocation(packet.name);
    }

    public static CPacketCloseApp decode(final FriendlyByteBuf buffer) {
        final var sessionId = buffer.readUUID();
        final var name = buffer.readResourceLocation();
        return new CPacketCloseApp(sessionId, name);
    }
}
