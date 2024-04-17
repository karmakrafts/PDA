/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.sb;

import io.karma.pda.api.common.app.AppState;
import io.karma.pda.api.common.util.JSONUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 17/04/2024
 */
public final class SPacketUpdateAppState {
    private final UUID sessionId;
    private final ResourceLocation name;
    private final AppState state;

    public SPacketUpdateAppState(final UUID sessionId, final @Nullable ResourceLocation name, final AppState state) {
        this.sessionId = sessionId;
        this.name = name;
        this.state = state;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public @Nullable ResourceLocation getName() {
        return name;
    }

    public AppState getState() {
        return state;
    }

    public static void encode(final SPacketUpdateAppState packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.sessionId);
        final var name = packet.name;
        if (name != null) {
            buffer.writeBoolean(true);
            buffer.writeResourceLocation(name);
        }
        else {
            buffer.writeBoolean(false);
        }
        buffer.writeByteArray(JSONUtils.compress(packet.state));
    }

    public static SPacketUpdateAppState decode(final FriendlyByteBuf buffer) {
        final var sessionId = buffer.readUUID();
        // @formatter:off
        final var name = buffer.readBoolean()
            ? buffer.readResourceLocation()
            : null;
        // @formatter:on
        final var state = JSONUtils.decompress(buffer.readByteArray(), AppState.class);
        return new SPacketUpdateAppState(sessionId, name, state);
    }
}
