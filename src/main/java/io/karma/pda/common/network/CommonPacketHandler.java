/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network;

import io.karma.pda.common.PDAMod;
import io.karma.pda.common.network.cb.CPacketCreateSession;
import io.karma.pda.common.network.sb.SPacketCreateSession;
import io.karma.pda.common.network.sb.SPacketSyncValues;
import io.karma.pda.common.network.sb.SPacketTerminateSession;
import io.karma.pda.common.session.DockedSessionContext;
import io.karma.pda.common.session.HandheldSessionContext;
import io.karma.pda.common.session.ServerSessionHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
public class CommonPacketHandler {
    public static final CommonPacketHandler INSTANCE = new CommonPacketHandler();

    // @formatter:off
    protected CommonPacketHandler() {}
    // @formatter:on

    private static <MSG> BiConsumer<MSG, Supplier<NetworkEvent.Context>> makeMessageHandler(
        final BiConsumer<MSG, NetworkEvent.Context> handler) {
        return (packet, contextSupplier) -> {
            final var context = contextSupplier.get();
            context.enqueueWork(() -> handler.accept(packet, context));
            context.setPacketHandled(true);
        };
    }

    @ApiStatus.Internal
    public void setup() {
        registerPacket(PacketIDs.SB_CREATE_SESSION,
            SPacketCreateSession.class,
            SPacketCreateSession::encode,
            SPacketCreateSession::decode,
            this::handleSPacketCreateSession);
        registerPacket(PacketIDs.SB_TERMINATE_SESSION,
            SPacketTerminateSession.class,
            SPacketTerminateSession::encode,
            SPacketTerminateSession::decode,
            this::handleSPacketTerminateSession);
        registerPacket(PacketIDs.SB_SYNC_VALUES,
            SPacketSyncValues.class,
            SPacketSyncValues::encode,
            SPacketSyncValues::decode,
            this::handleSPacketSyncValues);
    }

    protected <MSG> void registerPacket(final int id, final Class<MSG> type,
                                        final BiConsumer<MSG, FriendlyByteBuf> encoder,
                                        final Function<FriendlyByteBuf, MSG> decoder,
                                        final BiConsumer<MSG, NetworkEvent.Context> handler) {
        PDAMod.CHANNEL.registerMessage(id, type, encoder, decoder, makeMessageHandler(handler));
    }

    private void handleSPacketCreateSession(final SPacketCreateSession packet, final NetworkEvent.Context context) {
        final var player = context.getSender();
        final var session = ServerSessionHandler.INSTANCE.createSession(packet.getType().isHandheld() ? new HandheldSessionContext(
            player,
            packet.getHand()) : new DockedSessionContext(player, packet.getPos()));
        PDAMod.CHANNEL.reply(new CPacketCreateSession(packet.getRequestId(), session.getId()), context);
    }

    private void handleSPacketTerminateSession(final SPacketTerminateSession packet,
                                               final NetworkEvent.Context context) {
        ServerSessionHandler.INSTANCE.terminateSession(packet.getUUID());
    }

    private void handleSPacketSyncValues(final SPacketSyncValues packet, final NetworkEvent.Context context) {

    }
}
