/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.cb;

import io.karma.pda.api.common.util.TypedValue;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
public final class CPacketSyncValues {
    private final UUID sessionId;
    private final UUID playerId;
    private final Map<UUID, TypedValue<?>> values;

    public CPacketSyncValues(final UUID sessionId, final @Nullable UUID playerId,
                             final Map<UUID, TypedValue<?>> values) {
        this.sessionId = sessionId;
        this.playerId = playerId;
        this.values = values;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public @Nullable UUID getPlayerId() {
        return playerId;
    }

    public Map<UUID, TypedValue<?>> getValues() {
        return values;
    }

    public static void encode(final CPacketSyncValues packet, final FriendlyByteBuf buffer) {

    }

    public static CPacketSyncValues decode(final FriendlyByteBuf buffer) {
        return null;
    }
}
