/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.sb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.karma.pda.api.common.util.JSONUtils;
import net.minecraft.network.FriendlyByteBuf;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
public final class SPacketSyncValues {
    private final UUID sessionId;
    private final Map<UUID, JsonNode> values;

    public SPacketSyncValues(final UUID sessionId, final Map<UUID, JsonNode> values) {
        this.sessionId = sessionId;
        this.values = values;
    }

    public SPacketSyncValues(final UUID sessionId) {
        this(sessionId, new HashMap<>());
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public Map<UUID, JsonNode> getValues() {
        return values;
    }

    public static void encode(final SPacketSyncValues packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.sessionId);
        final var node = JSONUtils.MAPPER.createObjectNode();
        // @formatter:off
        node.setAll(packet.values.entrySet()
            .stream()
            .map(entry -> Pair.of(entry.getKey().toString(), entry.getValue()))
            .collect(Collectors.toMap(Pair::getLeft, Pair::getRight)));
        // @formatter:on
        buffer.writeByteArray(JSONUtils.compress(node));
    }

    public static SPacketSyncValues decode(final FriendlyByteBuf buffer) {
        final var sessionId = buffer.readUUID();
        final var node = Objects.requireNonNull(JSONUtils.decompress(buffer.readByteArray(), ObjectNode.class));
        // @formatter:off
        return new SPacketSyncValues(sessionId, node.properties()
            .stream()
            .map(entry -> Pair.of(UUID.fromString(entry.getKey()), entry.getValue()))
            .collect(Collectors.toMap(Pair::getLeft, Pair::getRight)));
        // @formatter:on
    }
}
