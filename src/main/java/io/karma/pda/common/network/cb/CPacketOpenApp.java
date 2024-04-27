/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.cb;

import io.karma.pda.common.util.PacketUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
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
    private final Map<String, ? extends List<UUID>> newIds;

    public CPacketOpenApp(final UUID sessionId, final UUID playerId, final ResourceLocation name,
                          final Map<String, ? extends List<UUID>> newIds) {
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

    public Map<String, ? extends List<UUID>> getNewIds() {
        return newIds;
    }

    public static void encode(final CPacketOpenApp packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.sessionId);
        buffer.writeResourceLocation(packet.name);
        buffer.writeUUID(packet.playerId);
        // @formatter:off
        PacketUtils.writeMap(packet.newIds, FriendlyByteBuf::writeUtf,
            (buf, val) -> PacketUtils.writeList(val, FriendlyByteBuf::writeUUID, buf), buffer);
        // @formatter:on
    }

    public static CPacketOpenApp decode(final FriendlyByteBuf buffer) {
        final var sessionId = buffer.readUUID();
        final var name = buffer.readResourceLocation();
        final var playerId = buffer.readUUID();
        // @formatter:off
        final var newIds = PacketUtils.readMap(buffer, FriendlyByteBuf::readUtf,
            buf -> PacketUtils.readList(buf, FriendlyByteBuf::readUUID));
        // @formatter:on
        return new CPacketOpenApp(sessionId, playerId, name, newIds);
    }
}
