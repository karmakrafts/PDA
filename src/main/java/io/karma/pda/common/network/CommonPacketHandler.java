/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network;

import io.karma.pda.api.common.API;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.network.cb.CPacketCreateSession;
import io.karma.pda.common.network.cb.CPacketOpenApp;
import io.karma.pda.common.network.sb.*;
import io.karma.pda.common.session.DefaultSessionHandler;
import io.karma.pda.common.session.DockedSessionContext;
import io.karma.pda.common.session.HandheldSessionContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
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

    @ApiStatus.Internal
    public void registerPackets() {
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
        registerPacket(PacketIDs.SB_OPEN_APP,
            SPacketOpenApp.class,
            SPacketOpenApp::encode,
            SPacketOpenApp::decode,
            this::handleSPacketOpenApp);
        registerPacket(PacketIDs.SB_CLOSE_APP,
            SPacketCloseApp.class,
            SPacketCloseApp::encode,
            SPacketCloseApp::decode,
            this::handleSPacketCloseApp);
    }

    protected <MSG> void registerPacket(final int id, final Class<MSG> type,
                                        final BiConsumer<MSG, FriendlyByteBuf> encoder,
                                        final Function<FriendlyByteBuf, MSG> decoder,
                                        final BiConsumer<MSG, Supplier<NetworkEvent.Context>> handler) {
        PDAMod.CHANNEL.registerMessage(id, type, encoder, decoder, (packet, contextGetter) -> {
            final var context = contextGetter.get();
            context.enqueueWork(() -> handler.accept(packet, contextGetter));
            context.setPacketHandled(true);
        });
    }

    private void handleSPacketCreateSession(final SPacketCreateSession packet,
                                            final Supplier<NetworkEvent.Context> contextGetter) {
        final var context = contextGetter.get();
        final var player = context.getSender();
        final var session = DefaultSessionHandler.INSTANCE.createSession(packet.getType().isHandheld() ? new HandheldSessionContext(
            player,
            packet.getHand()) : new DockedSessionContext(player, packet.getPos())).join();
        final var response = new CPacketCreateSession(packet.getRequestId(), session.getId());
        PDAMod.CHANNEL.reply(response, context);
    }

    private void handleSPacketTerminateSession(final SPacketTerminateSession packet,
                                               final Supplier<NetworkEvent.Context> contextGetter) {
        final var sessionHandler = DefaultSessionHandler.INSTANCE;
        final var session = sessionHandler.getActiveSession(packet.getId());
        if (session == null) {
            return;
        }
        sessionHandler.terminateSession(session);
    }

    private void handleSPacketSyncValues(final SPacketSyncValues packet,
                                         final Supplier<NetworkEvent.Context> contextGetter) {
        // TODO: ...
    }

    private void handleSPacketOpenApp(final SPacketOpenApp packet, final Supplier<NetworkEvent.Context> contextGetter) {
        final var sessionHandler = DefaultSessionHandler.INSTANCE;
        final var session = sessionHandler.getActiveSession(packet.getSessionId());
        if (session == null) {
            return;
        }
        final var app = session.getLauncher().openApp(API.getAppTypeRegistry().getValue(packet.getName())).join();
        final var typeName = app.getType().getName();
        final var response = new CPacketOpenApp(typeName, new ArrayList<>(app.getViews()));
        PDAMod.CHANNEL.reply(response, contextGetter.get());
    }

    private void handleSPacketCloseApp(final SPacketCloseApp packet,
                                       final Supplier<NetworkEvent.Context> contextGetter) {
        final var sessionHandler = DefaultSessionHandler.INSTANCE;
        final var session = sessionHandler.getActiveSession(packet.getSessionId());
        if (session == null) {
            return;
        }
        final var typeName = packet.getName();
        if (typeName == null) {
            session.getLauncher().closeApp();
            return;
        }
        session.getLauncher().closeApp(API.getAppTypeRegistry().getValue(typeName));
    }
}
