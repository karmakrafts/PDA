/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.sb;

import io.karma.pda.api.session.SessionContext;
import io.karma.pda.api.session.SessionType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 06/04/2024
 */
public final class SPacketCreateSession {
    private final SessionType type;
    private final UUID requestId;
    private final Object context; // InteractionHand or BlockPos

    public SPacketCreateSession(final SessionType type, final UUID requestId, final Object context) {
        this.type = type;
        this.requestId = requestId;
        this.context = context;
    }

    public static SPacketCreateSession fromContext(final UUID uuid, final SessionContext context) {
        final var type = context.getType();
        final var contextData = type == SessionType.DOCKED ? context.getPos() : context.getHand();
        return new SPacketCreateSession(type, uuid, contextData);
    }

    public static void encode(final SPacketCreateSession packet, final FriendlyByteBuf buffer) {
        final var type = packet.type;
        buffer.writeEnum(type);
        buffer.writeUUID(packet.requestId);
        if (type == SessionType.DOCKED) {
            buffer.writeBlockPos((BlockPos) packet.context);
            return;
        }
        buffer.writeEnum((InteractionHand) packet.context);
    }

    public static SPacketCreateSession decode(final FriendlyByteBuf buffer) {
        final var type = buffer.readEnum(SessionType.class);
        final var requestId = buffer.readUUID();
        if (type == SessionType.DOCKED) {
            return new SPacketCreateSession(type, requestId, buffer.readBlockPos());
        }
        return new SPacketCreateSession(type, requestId, buffer.readEnum(InteractionHand.class));
    }

    public SessionType getType() {
        return type;
    }

    public UUID getRequestId() {
        return requestId;
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
