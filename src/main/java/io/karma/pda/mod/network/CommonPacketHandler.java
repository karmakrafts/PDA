/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.network;

import io.karma.pda.mod.PDAMod;
import io.karma.pda.api.API;
import io.karma.pda.api.app.component.Component;
import io.karma.pda.api.app.component.Container;
import io.karma.pda.api.util.Exceptions;
import io.karma.pda.mod.network.cb.CPacketCloseApp;
import io.karma.pda.mod.network.cb.CPacketCreateSession;
import io.karma.pda.mod.network.cb.CPacketOpenApp;
import io.karma.pda.mod.network.cb.CPacketTerminateSession;
import io.karma.pda.mod.network.sb.*;
import io.karma.pda.mod.session.DefaultSessionHandler;
import io.karma.pda.mod.session.DockedSessionContext;
import io.karma.pda.mod.session.HandheldSessionContext;
import io.karma.pda.mod.util.TreeGraph;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

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

        PDAMod.CHANNEL.send(PacketDistributor.ALL.noArg(),
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

        final var player = Objects.requireNonNull(context.getSender());
        PDAMod.CHANNEL.send(PacketDistributor.ALL.noArg(),
            new CPacketTerminateSession(sessionId, player.getUUID(), true));
    }

    private void onOpenApp(final SPacketOpenApp packet, final NetworkEvent.Context context) {
        final var sessionHandler = DefaultSessionHandler.INSTANCE;
        final var session = sessionHandler.findById(packet.getSessionId());
        if (session == null) {
            return; // TODO: warn?
        }

        final var name = packet.getName();
        final var sessionId = session.getId();

        final var appType = API.getAppTypeRegistry().getValue(name);
        final var app = session.getLauncher().openApp(appType).join();
        final var mappings = new HashMap<String, ArrayList<UUID>>();

        if (app == null) {
            return; // TODO: warn?
        }
        for (final var view : app.getViews()) {
            final var viewName = view.getName();
            final var oldIds = packet.getOldIds().get(viewName);
            if (oldIds == null) {
                continue; // TODO: warn?
            }
            // @formatter:off
            final var newIds = TreeGraph.from(view.getContainer(),
                Container.class, Container::getChildren, Component::getId).flatten();
            // @formatter:on
            if (oldIds.size() != newIds.size()) {
                continue; // TODO: warn?
            }
            mappings.put(viewName, newIds);
        }

        final var playerId = Objects.requireNonNull(context.getSender()).getUUID();
        PDAMod.CHANNEL.send(PacketDistributor.ALL.noArg(), new CPacketOpenApp(sessionId, playerId, name, mappings));
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

        final var playerId = Objects.requireNonNull(context.getSender()).getUUID();
        PDAMod.CHANNEL.send(PacketDistributor.ALL.noArg(), new CPacketCloseApp(sessionId, playerId, typeName));
    }

    private void onSyncValues(final SPacketSyncValues packet, final NetworkEvent.Context context) {
        // TODO: ...
    }
}
