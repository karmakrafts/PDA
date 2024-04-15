/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.sb;

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

    public UUID getSessionId() {
        return sessionId;
    }

    public @Nullable ResourceLocation getName() {
        return name;
    }

    public static void encode(final SPacketCloseApp packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.sessionId);
        final var name = packet.name;
        if (name != null) {
            buffer.writeBoolean(true);
            buffer.writeResourceLocation(name);
        }
        else {
            buffer.writeBoolean(false);
        }
    }

    public static SPacketCloseApp decode(final FriendlyByteBuf buffer) {
        final UUID sessionId = buffer.readUUID();
        // @formatter:off
        final ResourceLocation name = buffer.readBoolean()
            ? buffer.readResourceLocation()
            : null;
        // @formatter:on
        return new SPacketCloseApp(sessionId, name);
    }
}
