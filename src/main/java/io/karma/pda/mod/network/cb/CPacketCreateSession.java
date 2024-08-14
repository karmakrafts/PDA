/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.network.cb;

import io.karma.pda.api.session.SessionType;
import io.karma.pda.mod.util.PacketUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
public final class CPacketCreateSession {
    private final SessionType type;
    private final UUID requestId;
    private final UUID sessionId;
    private final UUID playerId;
    private final Object context;

    public CPacketCreateSession(final SessionType type,
                                final @Nullable UUID requestId,
                                final UUID sessionId,
                                final UUID playerId,
                                final Object context) {
        this.requestId = requestId;
        this.sessionId = sessionId;
        this.playerId = playerId;
        this.type = type;
        this.context = context;
    }

    public static void encode(final CPacketCreateSession packet, final FriendlyByteBuf buffer) {
        final var type = packet.type;
        buffer.writeEnum(type);
        PacketUtils.writeNullable(packet.requestId, FriendlyByteBuf::writeUUID, buffer);
        buffer.writeUUID(packet.sessionId);
        buffer.writeUUID(packet.playerId);
        if (type == SessionType.DOCKED) {
            buffer.writeBlockPos((BlockPos) packet.context);
            return;
        }
        buffer.writeEnum((InteractionHand) packet.context);
    }

    public static CPacketCreateSession decode(final FriendlyByteBuf buffer) {
        final var type = buffer.readEnum(SessionType.class);
        final var requestId = PacketUtils.readNullable(buffer, FriendlyByteBuf::readUUID);
        final var sessionId = buffer.readUUID();
        final var playerId = buffer.readUUID();
        if (type == SessionType.DOCKED) {
            return new CPacketCreateSession(type, requestId, sessionId, playerId, buffer.readBlockPos());
        }
        return new CPacketCreateSession(type, requestId, sessionId, playerId, buffer.readEnum(InteractionHand.class));
    }

    public @Nullable UUID getRequestId() {
        return requestId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public SessionType getType() {
        return type;
    }

    public Object getContext() {
        return context;
    }

    public @Nullable InteractionHand getHand() {
        if (!(context instanceof InteractionHand hand)) {
            return null;
        }
        return hand;
    }

    public @Nullable BlockPos getPos() {
        if (!(context instanceof BlockPos pos)) {
            return null;
        }
        return pos;
    }
}
