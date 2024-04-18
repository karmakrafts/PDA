/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network;

import io.karma.pda.api.common.API;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.network.cb.CPacketCloseApp;
import io.karma.pda.common.network.cb.CPacketCreateSession;
import io.karma.pda.common.network.cb.CPacketOpenApp;
import io.karma.pda.common.network.cb.CPacketTerminateSession;
import io.karma.pda.common.network.sb.*;
import io.karma.pda.common.session.DefaultSessionHandler;
import io.karma.pda.common.session.DockedSessionContext;
import io.karma.pda.common.session.HandheldSessionContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

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
    public void registerPackets() { // @formatter:off
        registerPacket(PacketIDs.SB_CREATE_SESSION,
            SPacketCreateSession.class, SPacketCreateSession::encode, SPacketCreateSession::decode,
            this::onCreateSession);
        registerPacket(PacketIDs.SB_TERMINATE_SESSION,
            SPacketTerminateSession.class, SPacketTerminateSession::encode, SPacketTerminateSession::decode,
            this::onTerminateSession);
        registerPacket(PacketIDs.SB_OPEN_APP,
            SPacketOpenApp.class, SPacketOpenApp::encode, SPacketOpenApp::decode,
            this::onOpenApp);
        registerPacket(PacketIDs.SB_CLOSE_APP,
            SPacketCloseApp.class, SPacketCloseApp::encode, SPacketCloseApp::decode,
            this::onCloseApp);
        registerPacket(PacketIDs.SB_ADD_SYNC_VALUE,
            SPacketAddSyncValue.class, SPacketAddSyncValue::encode, SPacketAddSyncValue::decode,
            this::onAddSyncValue);
        registerPacket(PacketIDs.SB_REMOVE_SYNC_VALUE,
            SPacketRemoveSyncValue.class, SPacketRemoveSyncValue::encode, SPacketRemoveSyncValue::decode,
            this::onRemoveSyncValue);
        registerPacket(PacketIDs.SB_SYNC_VALUES,
            SPacketSyncValues.class, SPacketSyncValues::encode, SPacketSyncValues::decode,
            this::onSyncValues);
    } // @formatter:on

    private static PacketDistributor<ServerPlayer> allMatching(final Predicate<ServerPlayer> predicate) {
        return new PacketDistributor<>((distributor, supplier) -> {
            final var player = supplier.get();
            return packet -> {
                if (packet.isSkippable() && !predicate.test(player)) {
                    return;
                }
                player.connection.connection.send(packet);
            };
        }, NetworkDirection.PLAY_TO_CLIENT);
    }

    private static void sendToAllExcept(final ServerPlayer player, final Object message) {
        PDAMod.CHANNEL.send(allMatching(p -> !p.getUUID().equals(player.getUUID())).noArg(), message);
    }

    protected <MSG> void registerPacket(final int id, final Class<MSG> type,
                                        final BiConsumer<MSG, FriendlyByteBuf> encoder,
                                        final Function<FriendlyByteBuf, MSG> decoder,
                                        final BiConsumer<MSG, NetworkEvent.Context> handler) {
        PDAMod.CHANNEL.registerMessage(id, type, encoder, decoder, (packet, contextGetter) -> {
            final var context = contextGetter.get();
            context.enqueueWork(() -> handler.accept(packet, context));
            context.setPacketHandled(true);
        });
    }

    private void onCreateSession(final SPacketCreateSession packet, final NetworkEvent.Context context) {
        final var player = Objects.requireNonNull(context.getSender());
        final var type = packet.getType();
        final var session = DefaultSessionHandler.INSTANCE.createSession(type.isHandheld() ? new HandheldSessionContext(
            player,
            packet.getHand()) : new DockedSessionContext(player, packet.getPos())).join();
        final var requestId = packet.getRequestId();
        final var sessionId = session.getId();
        // Reply to sender client with new session
        PDAMod.CHANNEL.reply(new CPacketCreateSession(type, requestId, sessionId, null, packet.getContext()), context);
        // Broadcast new session to all remaining clients
        sendToAllExcept(player,
            new CPacketCreateSession(type, requestId, sessionId, player.getUUID(), packet.getContext()));
    }

    private void onTerminateSession(final SPacketTerminateSession packet, final NetworkEvent.Context context) {
        final var sessionHandler = DefaultSessionHandler.INSTANCE;
        final var session = sessionHandler.getActiveSession(packet.getId());
        if (session == null) {
            return;
        }
        sessionHandler.terminateSession(session);
        final var sessionId = session.getId();
        // Reply to sender client with termination acknowledgement
        PDAMod.CHANNEL.reply(new CPacketTerminateSession(sessionId, null), context);
        // Broadcast termination acknowledgement to remaining clients
        final var player = Objects.requireNonNull(context.getSender());
        sendToAllExcept(player, new CPacketTerminateSession(sessionId, player.getUUID()));
    }

    private void onOpenApp(final SPacketOpenApp packet, final NetworkEvent.Context context) {
        final var sessionHandler = DefaultSessionHandler.INSTANCE;
        final var session = sessionHandler.getActiveSession(packet.getSessionId());
        if (session == null) {
            return;
        }
        final var app = session.getLauncher().openApp(API.getAppTypeRegistry().getValue(packet.getName())).join();
        final var typeName = app.getType().getName();
        final var sessionId = session.getId();
        // Reply to sender client with compressed app layout
        PDAMod.CHANNEL.reply(new CPacketOpenApp(sessionId, null, typeName, new ArrayList<>(app.getViews())), context);
        // Broadcast compressed app layout to all remaining clients
        final var player = Objects.requireNonNull(context.getSender());
        sendToAllExcept(player,
            new CPacketOpenApp(sessionId, player.getUUID(), typeName, new ArrayList<>(app.getViews())));
    }

    private void onCloseApp(final SPacketCloseApp packet, final NetworkEvent.Context context) {
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
        session.getLauncher().closeApp(API.getAppTypeRegistry().getValue(typeName)); // TODO: join here?
        final var sessionId = session.getId();
        // Reply to sender client with close acknowledgement
        PDAMod.CHANNEL.reply(new CPacketCloseApp(sessionId, null, typeName), context);
        // Broadcast close acknowledgement to remaining clients
        final var player = Objects.requireNonNull(context.getSender());
        sendToAllExcept(player, new CPacketCloseApp(sessionId, player.getUUID(), typeName));
    }

    private void onAddSyncValue(final SPacketAddSyncValue packet, final NetworkEvent.Context context) {
        // TODO: ...
    }

    private void onRemoveSyncValue(final SPacketRemoveSyncValue packet, final NetworkEvent.Context context) {
        // TODO: ...
    }

    private void onSyncValues(final SPacketSyncValues packet, final NetworkEvent.Context context) {
        // TODO: ...
    }
}
