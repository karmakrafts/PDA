/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network;

import io.karma.pda.common.PDAMod;
import net.minecraft.network.FriendlyByteBuf;
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
    private int packetId = 1;

    // @formatter:off
    private CommonPacketHandler() {}
    // @formatter:on

    public void setup() {
        registerPacket(SPacketCreateSession.class,
            SPacketCreateSession::encode,
            SPacketCreateSession::decode,
            this::handleSPacketCreateSession);
        registerPacket(SPacketTerminateSession.class,
            SPacketTerminateSession::encode,
            SPacketTerminateSession::decode,
            this::handleSPacketTerminateSession);
    }

    private <MSG> void registerPacket(final Class<MSG> type, final BiConsumer<MSG, FriendlyByteBuf> encoder,
                                      final Function<FriendlyByteBuf, MSG> decoder,
                                      final BiConsumer<MSG, Supplier<NetworkEvent.Context>> handler) {
        PDAMod.CHANNEL.registerMessage(packetId++, type, encoder, decoder, handler);
    }

    private void handleSPacketCreateSession(final SPacketCreateSession packet,
                                            final Supplier<NetworkEvent.Context> contextSupplier) {

    }

    private void handleSPacketTerminateSession(final SPacketTerminateSession packet,
                                               final Supplier<NetworkEvent.Context> contextSupplier) {

    }
}
