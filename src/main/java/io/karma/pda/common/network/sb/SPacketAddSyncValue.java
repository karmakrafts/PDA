/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.sb;

import io.karma.pda.api.common.util.JSONUtils;
import io.karma.pda.api.common.util.TypedValue;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 18/04/2024
 */
public final class SPacketAddSyncValue {
    private final UUID sessionId;
    private final UUID initialId;
    private final TypedValue<?> initialValue;

    public SPacketAddSyncValue(final UUID sessionId, final UUID initialId, final TypedValue<?> initialValue) {
        this.sessionId = sessionId;
        this.initialId = initialId;
        this.initialValue = initialValue;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public UUID getInitialId() {
        return initialId;
    }

    public TypedValue<?> getInitialValue() {
        return initialValue;
    }

    public static void encode(final SPacketAddSyncValue packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.sessionId);
        buffer.writeUUID(packet.initialId);
        buffer.writeByteArray(JSONUtils.compress(packet.initialValue.get()));
    }

    public static SPacketAddSyncValue decode(final FriendlyByteBuf buffer) {
        final var sessionId = buffer.readUUID();
        final var initialId = buffer.readUUID();
        final var initialValue = TypedValue.fromPair(JSONUtils.decompress(buffer.readByteArray()));
        return new SPacketAddSyncValue(sessionId, initialId, initialValue);
    }
}
