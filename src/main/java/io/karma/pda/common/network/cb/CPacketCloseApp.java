/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.cb;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 17/04/2024
 */
public final class CPacketCloseApp {
    private final UUID sessionId;
    private final UUID playerId;
    private final ResourceLocation name;

    public CPacketCloseApp(final UUID sessionId, final @Nullable UUID playerId, final ResourceLocation name) {
        this.sessionId = sessionId;
        this.playerId = playerId;
        this.name = name;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public @Nullable UUID getPlayerId() {
        return playerId;
    }

    public ResourceLocation getName() {
        return name;
    }

    public static void encode(final CPacketCloseApp packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.sessionId);
        final var playerId = packet.playerId;
        if (playerId != null) {
            buffer.writeBoolean(true);
            buffer.writeUUID(playerId);
        }
        else {
            buffer.writeBoolean(false);
        }
        buffer.writeResourceLocation(packet.name);
    }

    public static CPacketCloseApp decode(final FriendlyByteBuf buffer) {
        final var sessionId = buffer.readUUID();
        final var playerId = buffer.readBoolean() ? buffer.readUUID() : null;
        final var name = buffer.readResourceLocation();
        return new CPacketCloseApp(sessionId, playerId, name);
    }
}
