/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.sb;

import io.karma.pda.api.common.util.TypedValue;
import net.minecraft.network.FriendlyByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
public final class SPacketSyncValues {
    private final UUID sessionId;
    private final Map<UUID, TypedValue<?>> values;

    public SPacketSyncValues(final UUID sessionId, final Map<UUID, TypedValue<?>> values) {
        this.sessionId = sessionId;
        this.values = values;
    }

    public SPacketSyncValues(final UUID sessionId) {
        this(sessionId, new HashMap<>());
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public Map<UUID, TypedValue<?>> getValues() {
        return values;
    }

    public static void encode(final SPacketSyncValues packet, final FriendlyByteBuf buffer) {

    }

    public static SPacketSyncValues decode(final FriendlyByteBuf buffer) {
        return null;
    }
}
