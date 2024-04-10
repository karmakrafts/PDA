/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network;

import io.karma.pda.api.common.session.DockedSessionContext;
import io.karma.pda.api.common.session.HandheldSessionContext;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.session.SessionDataHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
public final class CommonPacketHandler {
    public static final CommonPacketHandler INSTANCE = new CommonPacketHandler();

    // @formatter:off
    private CommonPacketHandler() {}
    // @formatter:on

    private static <MSG> BiConsumer<MSG, Supplier<NetworkEvent.Context>> makeMessageHandler(
        final BiConsumer<MSG, Player> handler) {
        return (packet, contextSupplier) -> {
            final var context = contextSupplier.get();
            context.enqueueWork(() -> {
                Player player = context.getSender();
                if (player == null && FMLEnvironment.dist == Dist.CLIENT) {
                    player = Minecraft.getInstance().player;
                }
                handler.accept(packet, player);
            });
            context.setPacketHandled(true);
        };
    }

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
    }

    private <MSG> void registerPacket(final int id, final Class<MSG> type,
                                      final BiConsumer<MSG, FriendlyByteBuf> encoder,
                                      final Function<FriendlyByteBuf, MSG> decoder,
                                      final BiConsumer<MSG, Player> handler) {
        PDAMod.CHANNEL.registerMessage(id, type, encoder, decoder, makeMessageHandler(handler));
    }

    private void handleSPacketCreateSession(final SPacketCreateSession packet, final Player player) {
        SessionDataHandler.INSTANCE.createSession(packet.getUUID(),
            packet.getType().isHandheld() ? new HandheldSessionContext(player,
                packet.getHand()) : new DockedSessionContext(player, packet.getPos()));
    }

    private void handleSPacketTerminateSession(final SPacketTerminateSession packet, final Player player) {
        SessionDataHandler.INSTANCE.terminateSession(packet.getUUID());
    }
}
