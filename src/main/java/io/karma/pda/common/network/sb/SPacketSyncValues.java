/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.sb;

import io.karma.pda.api.common.sync.Synced;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
public final class SPacketSyncValues {
    private final UUID sessionId;
    private final Map<UUID, Synced<?>> values;

    public SPacketSyncValues(final UUID sessionId, final Map<UUID, Synced<?>> values) {
        this.sessionId = sessionId;
        this.values = values;
    }

    public SPacketSyncValues(final UUID sessionId) {
        this(sessionId, new HashMap<>());
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public Map<UUID, Synced<?>> getValues() {
        return values;
    }

    public static void encode(final SPacketSyncValues packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.sessionId);
        // TODO: ...
    }

    public static SPacketSyncValues decode(final FriendlyByteBuf buffer) {
        final var sessionId = buffer.readUUID();
        // TODO: ...
        return new SPacketSyncValues(sessionId, Collections.emptyMap());
    }
}
