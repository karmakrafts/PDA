/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.cb;

import io.karma.pda.api.common.app.AppState;
import io.karma.pda.api.common.util.JSONUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 17/04/2024
 */
public final class CPacketUpdateAppState {
    private final UUID sessionId;
    private final ResourceLocation name;
    private final AppState state;

    public CPacketUpdateAppState(final UUID sessionId, final ResourceLocation name, final AppState state) {
        this.sessionId = sessionId;
        this.name = name;
        this.state = state;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public ResourceLocation getName() {
        return name;
    }

    public AppState getState() {
        return state;
    }

    public static void encode(final CPacketUpdateAppState packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.sessionId);
        buffer.writeResourceLocation(packet.name);
        buffer.writeByteArray(JSONUtils.compress(packet.state));
    }

    public static CPacketUpdateAppState decode(final FriendlyByteBuf buffer) {
        final var sessionId = buffer.readUUID();
        final var name = buffer.readResourceLocation();
        final var state = JSONUtils.decompress(buffer.readByteArray(), AppState.class);
        return new CPacketUpdateAppState(sessionId, name, state);
    }
}
