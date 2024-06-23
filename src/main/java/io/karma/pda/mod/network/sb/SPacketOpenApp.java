/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.network.sb;

import io.karma.pda.mod.util.PacketUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 14/04/2024
 */
public final class SPacketOpenApp {
    private final UUID sessionId;
    private final ResourceLocation name;
    private final Map<String, List<UUID>> oldIds;

    public SPacketOpenApp(final UUID sessionId, final ResourceLocation name, final Map<String, List<UUID>> oldIds) {
        this.sessionId = sessionId;
        this.name = name;
        this.oldIds = oldIds;
    }

    public static void encode(final SPacketOpenApp packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.sessionId);
        buffer.writeResourceLocation(packet.name);
        // @formatter:off
        PacketUtils.writeMap(packet.oldIds, FriendlyByteBuf::writeUtf,
            (buf, val) -> PacketUtils.writeList(val, FriendlyByteBuf::writeUUID, buf), buffer);
        // @formatter:on
    }

    public static SPacketOpenApp decode(final FriendlyByteBuf buffer) {
        final var sessionId = buffer.readUUID();
        final var name = buffer.readResourceLocation();
        // @formatter:off
        final var oldIds = PacketUtils.readMap(buffer, FriendlyByteBuf::readUtf,
            buf -> PacketUtils.readList(buf, FriendlyByteBuf::readUUID));
        // @formatter:on
        return new SPacketOpenApp(sessionId, name, oldIds);
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public ResourceLocation getName() {
        return name;
    }

    public Map<String, List<UUID>> getOldIds() {
        return oldIds;
    }
}
