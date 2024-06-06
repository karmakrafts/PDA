/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.network.sb;

import io.karma.pda.util.PacketUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 14/04/2024
 */
public final class SPacketCloseApp {
    private final UUID sessionId;
    private final ResourceLocation name;

    public SPacketCloseApp(final UUID sessionId, final @Nullable ResourceLocation name) {
        this.sessionId = sessionId;
        this.name = name;
    }

    public static void encode(final SPacketCloseApp packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.sessionId);
        PacketUtils.writeNullable(packet.name, FriendlyByteBuf::writeResourceLocation, buffer);
    }

    public static SPacketCloseApp decode(final FriendlyByteBuf buffer) {
        final var sessionId = buffer.readUUID();
        final var name = PacketUtils.readNullable(buffer, FriendlyByteBuf::readResourceLocation);
        return new SPacketCloseApp(sessionId, name);
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public @Nullable ResourceLocation getName() {
        return name;
    }
}
