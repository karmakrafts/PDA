/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.cb;

import io.karma.pda.common.util.PacketUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 14/04/2024
 */
public final class CPacketOpenApp {
    private final UUID sessionId;
    private final UUID playerId;
    private final ResourceLocation name;
    private final Map<UUID, UUID> newIds;

    public CPacketOpenApp(final UUID sessionId, final UUID playerId, final ResourceLocation name,
                          final Map<UUID, UUID> newIds) {
        this.sessionId = sessionId;
        this.playerId = playerId;
        this.name = name;
        this.newIds = newIds;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public ResourceLocation getName() {
        return name;
    }

    public Map<UUID, UUID> getNewIds() {
        return newIds;
    }

    public static void encode(final CPacketOpenApp packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.sessionId);
        buffer.writeResourceLocation(packet.name);
        buffer.writeUUID(packet.playerId);
        PacketUtils.writeMap(packet.newIds, FriendlyByteBuf::writeUUID, FriendlyByteBuf::writeUUID, buffer);
    }

    public static CPacketOpenApp decode(final FriendlyByteBuf buffer) {
        final var sessionId = buffer.readUUID();
        final var name = buffer.readResourceLocation();
        final var playerId = buffer.readUUID();
        final var newIds = PacketUtils.readMap(buffer, FriendlyByteBuf::readUUID, FriendlyByteBuf::readUUID);
        return new CPacketOpenApp(sessionId, playerId, name, newIds);
    }
}
