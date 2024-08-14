/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.network.cb;

import io.karma.pda.api.state.MutableState;
import io.karma.pda.api.state.State;
import io.karma.pda.api.util.JSONUtils;
import io.karma.pda.mod.util.PacketUtils;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
public final class CPacketSyncValues {
    private final UUID sessionId;
    private final UUID playerId;
    private final Map<String, ? extends Map<String, ? extends State<?>>> values;

    public CPacketSyncValues(final UUID sessionId,
                             final @Nullable UUID playerId,
                             final Map<String, ? extends Map<String, ? extends State<?>>> values) {
        this.sessionId = sessionId;
        this.playerId = playerId;
        this.values = values;
    }

    public static void encode(final CPacketSyncValues packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.sessionId);
        PacketUtils.writeNullable(packet.playerId, FriendlyByteBuf::writeUUID, buffer);
        // @formatter:off
        PacketUtils.writeMap(packet.values, FriendlyByteBuf::writeUtf,
            (b, map) -> PacketUtils.writeMap(map, FriendlyByteBuf::writeUtf,
                (buf, state) -> buf.writeByteArray(JSONUtils.compress(state.get())), b), buffer);
        // @formatter:on
    }

    public static CPacketSyncValues decode(final FriendlyByteBuf buffer) {
        final var sessionId = buffer.readUUID();
        final var playerId = PacketUtils.readNullable(buffer, FriendlyByteBuf::readUUID);
        // @formatter:off
        final var values = PacketUtils.readMap(buffer, FriendlyByteBuf::readUtf,
            b -> PacketUtils.readMap(b, FriendlyByteBuf::readUtf,
                buf -> MutableState.fromPair(Objects.requireNonNull(JSONUtils.decompress(buf.readByteArray())))));
        // @formatter:on
        return new CPacketSyncValues(sessionId, playerId, values);
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public @Nullable UUID getPlayerId() {
        return playerId;
    }

    public Map<String, ? extends Map<String, ? extends State<?>>> getValues() {
        return values;
    }
}
