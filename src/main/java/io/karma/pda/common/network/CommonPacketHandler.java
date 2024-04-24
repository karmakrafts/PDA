/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network;

import io.karma.pda.api.common.API;
import io.karma.pda.api.common.app.component.Component;
import io.karma.pda.api.common.app.component.Container;
import io.karma.pda.api.common.util.Exceptions;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.network.cb.CPacketCloseApp;
import io.karma.pda.common.network.cb.CPacketCreateSession;
import io.karma.pda.common.network.cb.CPacketOpenApp;
import io.karma.pda.common.network.cb.CPacketTerminateSession;
import io.karma.pda.common.network.sb.*;
import io.karma.pda.common.session.DefaultSessionHandler;
import io.karma.pda.common.session.DockedSessionContext;
import io.karma.pda.common.session.HandheldSessionContext;
import io.karma.pda.common.util.TreeGraph;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
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
        registerPacket(PacketIDs.SB_SYNC_VALUES,
            SPacketSyncValues.class, SPacketSyncValues::encode, SPacketSyncValues::decode,
            this::onSyncValues);
    } // @formatter:on

    private static PacketDistributor<ServerPlayer> allMatching(final Predicate<ServerPlayer> predicate) {
        return new PacketDistributor<>((distributor, supplier) -> {
            final var player = supplier.get();
            if (player == null) {
                return packet -> { // TODO: probably broken
                };
            }
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
            context.enqueueWork(() -> {
                try {
                    handler.accept(packet, context);
                }
                catch (Throwable error) {
                    PDAMod.LOGGER.error("Could not handle packet {}: {}", packet, Exceptions.toFancyString(error));
                }
            });
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
        final var session = sessionHandler.findById(packet.getId());
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
        final var session = sessionHandler.findById(packet.getSessionId());
        if (session == null) {
            return; // TODO: warn?
        }

        final var name = packet.getName();
        final var sessionId = session.getId();
        final var mappings = new HashMap<UUID, UUID>();

        final var appType = API.getAppTypeRegistry().getValue(name);
        final var app = session.getLauncher().openApp(appType).join();
        if (app == null) {
            return; // TODO: warn?
        }

        for (final var view : app.getViews()) {
            final var oldIds = packet.getOldIds().get(view.getName());
            if (oldIds == null) {
                continue; // TODO: warn?
            }
            // @formatter:off
            final var newIds = TreeGraph.from(view.getContainer(),
                Container.class, Container::getChildren, Component::getId).flatten();
            // @formatter:on
            final var numIds = oldIds.size();
            if (numIds != newIds.size()) {
                continue; // TODO: warn?
            }
            for (var i = 0; i < numIds; i++) {
                mappings.put(oldIds.get(i), newIds.get(i));
            }
        }

        // Reply to sender client with compressed app layout
        PDAMod.CHANNEL.reply(new CPacketOpenApp(sessionId, null, name, mappings), context);
        // Broadcast compressed app layout to all remaining clients
        final var player = Objects.requireNonNull(context.getSender());
        sendToAllExcept(player, new CPacketOpenApp(sessionId, player.getUUID(), name, mappings));
    }

    private void onCloseApp(final SPacketCloseApp packet, final NetworkEvent.Context context) {
        final var sessionHandler = DefaultSessionHandler.INSTANCE;
        final var session = sessionHandler.findById(packet.getSessionId());
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

    private void onSyncValues(final SPacketSyncValues packet, final NetworkEvent.Context context) {
        // TODO: ...
    }
}
