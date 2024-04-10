/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network;

import io.karma.pda.api.common.session.SessionContext;
import io.karma.pda.api.common.session.SessionType;
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
    private final UUID uuid;
    private final UUID playerUuid;
    private final Object context; // InteractionHand or BlockPos

    public SPacketCreateSession(final SessionType type, final UUID uuid, final UUID playerUuid, final Object context) {
        this.type = type;
        this.uuid = uuid;
        this.playerUuid = playerUuid;
        this.context = context;
    }

    public static SPacketCreateSession fromContext(final UUID uuid, final SessionContext context) {
        final var type = context.getType();
        final var contextData = type == SessionType.DOCKED ? context.getPos() : context.getHand();
        return new SPacketCreateSession(type, uuid, context.getPlayer().getUUID(), contextData);
    }

    public static void encode(final SPacketCreateSession packet, final FriendlyByteBuf buffer) {
        buffer.writeEnum(packet.type);
        buffer.writeUUID(packet.uuid);
        buffer.writeUUID(packet.playerUuid);
        final var context = packet.context;
        if (context instanceof InteractionHand hand) {
            buffer.writeEnum(hand);
            return;
        }
        buffer.writeBlockPos((BlockPos) context);
    }

    public static SPacketCreateSession decode(final FriendlyByteBuf buffer) {
        final var type = buffer.readEnum(SessionType.class);
        final var uuid = buffer.readUUID();
        final var playerUuid = buffer.readUUID();
        if (type == SessionType.DOCKED) {
            return new SPacketCreateSession(type, uuid, playerUuid, buffer.readBlockPos());
        }
        return new SPacketCreateSession(type, uuid, playerUuid, buffer.readEnum(InteractionHand.class));
    }

    public SessionType getType() {
        return type;
    }

    public UUID getUUID() {
        return uuid;
    }

    public UUID getPlayerUUID() {
        return playerUuid;
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
