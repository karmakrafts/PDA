/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.cb;

import io.karma.pda.api.common.session.SessionType;
import io.karma.pda.common.util.PacketUtils;
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

    public CPacketCreateSession(final SessionType type, final UUID requestId, final UUID sessionId,
                                final @Nullable UUID playerId, final Object context) {
        this.requestId = requestId;
        this.sessionId = sessionId;
        this.playerId = playerId;
        this.type = type;
        this.context = context;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public @Nullable UUID getPlayerId() {
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

    public static void encode(final CPacketCreateSession packet, final FriendlyByteBuf buffer) {
        final var type = packet.type;
        buffer.writeEnum(type);
        buffer.writeUUID(packet.requestId);
        buffer.writeUUID(packet.sessionId);
        PacketUtils.writeNullable(packet.playerId, FriendlyByteBuf::writeUUID, buffer);
        if (type == SessionType.DOCKED) {
            buffer.writeBlockPos((BlockPos) packet.context);
            return;
        }
        buffer.writeEnum((InteractionHand) packet.context);
    }

    public static CPacketCreateSession decode(final FriendlyByteBuf buffer) {
        final var type = buffer.readEnum(SessionType.class);
        final var requestId = buffer.readUUID();
        final var sessionId = buffer.readUUID();
        final var playerId = PacketUtils.readNullable(buffer, FriendlyByteBuf::readUUID);
        if (type == SessionType.DOCKED) {
            return new CPacketCreateSession(type, requestId, sessionId, playerId, buffer.readBlockPos());
        }
        return new CPacketCreateSession(type, requestId, sessionId, playerId, buffer.readEnum(InteractionHand.class));
    }
}
