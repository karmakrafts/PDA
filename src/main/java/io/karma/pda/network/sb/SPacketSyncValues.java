/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.network.sb;

import io.karma.pda.api.state.MutableState;
import io.karma.pda.api.state.State;
import io.karma.pda.api.util.JSONUtils;
import io.karma.pda.util.PacketUtils;
import net.minecraft.network.FriendlyByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
public final class SPacketSyncValues {
    private final UUID sessionId;
    private final Map<String, ? extends Map<String, ? extends State<?>>> values;

    public SPacketSyncValues(final UUID sessionId,
                             final Map<String, ? extends Map<String, ? extends State<?>>> values) {
        this.sessionId = sessionId;
        this.values = values;
    }

    public SPacketSyncValues(final UUID sessionId) {
        this(sessionId, new HashMap<>());
    }

    public static void encode(final SPacketSyncValues packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.sessionId);
        // @formatter:off
        PacketUtils.writeMap(packet.values, FriendlyByteBuf::writeUtf,
            (b, map) -> PacketUtils.writeMap(map, FriendlyByteBuf::writeUtf,
                (buf, state) -> buf.writeByteArray(JSONUtils.compress(state.get())), b), buffer);
        // @formatter:on
    }

    public static SPacketSyncValues decode(final FriendlyByteBuf buffer) {
        final var sessionId = buffer.readUUID();
        // @formatter:off
        final var values = PacketUtils.readMap(buffer, FriendlyByteBuf::readUtf,
            b -> PacketUtils.readMap(b, FriendlyByteBuf::readUtf,
                buf -> MutableState.fromPair(Objects.requireNonNull(JSONUtils.decompress(buf.readByteArray())))));
        // @formatter:on
        return new SPacketSyncValues(sessionId, values);
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public Map<String, ? extends Map<String, ? extends State<?>>> getValues() {
        return values;
    }
}
