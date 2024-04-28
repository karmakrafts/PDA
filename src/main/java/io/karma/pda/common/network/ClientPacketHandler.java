/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network;

import io.karma.pda.api.common.API;
import io.karma.pda.api.common.app.component.Component;
import io.karma.pda.api.common.app.component.Container;
import io.karma.pda.client.app.ClientLauncher;
import io.karma.pda.client.screen.DockScreen;
import io.karma.pda.client.screen.PDAScreen;
import io.karma.pda.client.session.ClientSession;
import io.karma.pda.client.session.ClientSessionHandler;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.network.cb.*;
import io.karma.pda.common.session.DockedSessionContext;
import io.karma.pda.common.session.HandheldSessionContext;
import io.karma.pda.common.util.TreeGraph;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
// THIS CANNOT
public final class ClientPacketHandler extends CommonPacketHandler {
    public static final ClientPacketHandler INSTANCE = new ClientPacketHandler();

    // @formatter:off
    private ClientPacketHandler() {}
    // @formatter:on

    @ApiStatus.Internal
    public void registerPackets() { // @formatter:off
        registerPacket(PacketIDs.CB_CREATE_SESSION,
            CPacketCreateSession.class, CPacketCreateSession::encode, CPacketCreateSession::decode,
            this::onCreateSession);
        registerPacket(PacketIDs.CB_TERMINATE_SESSION,
            CPacketTerminateSession.class, CPacketTerminateSession::encode, CPacketTerminateSession::decode,
            this::onTerminateSession);
        registerPacket(PacketIDs.CB_OPEN_APP,
            CPacketOpenApp.class, CPacketOpenApp::encode, CPacketOpenApp::decode,
            this::onOpenApp);
        registerPacket(PacketIDs.CB_CLOSE_APP,
            CPacketCloseApp.class, CPacketCloseApp::encode, CPacketCloseApp::decode,
            this::onCloseApp);
        registerPacket(PacketIDs.CB_SYNC_VALUES,
            CPacketSyncValues.class, CPacketSyncValues::encode, CPacketSyncValues::decode,
            this::onSyncValues);
        registerPacket(PacketIDs.CB_CANCEL_INTERACTION,
            CPacketCancelInteraction.class, CPacketCancelInteraction::encode, CPacketCancelInteraction::decode,
            this::onCancelInteraction);
    } // @formatter:on

    private static @Nullable Player getPlayerById(final UUID id) {
        final var level = Minecraft.getInstance().level;
        if (level == null) {
            return null;
        }
        return level.getPlayerByUUID(id);
    }

    private static boolean isLocalPlayer(final UUID id) {
        final var player = Minecraft.getInstance().player;
        return player != null && player.getUUID().equals(id);
    }

    private void onCancelInteraction(final CPacketCancelInteraction packet, final NetworkEvent.Context context) {
        final var game = Minecraft.getInstance();
        final var screen = game.screen;
        if (screen instanceof DockScreen || screen instanceof PDAScreen) {
            game.popGuiLayer(); // Just close the current GUI layer
        }
    }

    private void onCreateSession(final CPacketCreateSession packet, final NetworkEvent.Context context) {
        final var sessionHandler = ClientSessionHandler.INSTANCE;
        final var playerId = packet.getPlayerId();
        final var sessionId = packet.getSessionId();
        if (isLocalPlayer(playerId)) {
            sessionHandler.addPendingSession(packet.getRequestId(), sessionId);
            return;
        }
        final var extPlayer = getPlayerById(playerId);
        if (extPlayer == null) {
            PDAMod.LOGGER.warn("Could not find player with ID {}", playerId);
            return;
        }
        final var contextObj = packet.getContext();
        if (contextObj instanceof BlockPos pos) {
            sessionHandler.addActiveSession(sessionId,
                new ClientSession(sessionId, new DockedSessionContext(extPlayer, pos)));
            return;
        }
        sessionHandler.addActiveSession(sessionId,
            new ClientSession(sessionId, new HandheldSessionContext(extPlayer, (InteractionHand) contextObj)));
    }

    private static void remapComponentIds(final Component component, final List<UUID> ids) {
        final var components = TreeGraph.from(component, Container.class, Container::getChildren).flatten();
        final var numComponents = components.size();
        if (numComponents != ids.size()) {
            return; // TODO: warn?
        }
        for (var i = 0; i < numComponents; i++) {
            final var comp = components.get(i);
            final var oldId = comp.getId();
            final var newId = ids.get(i);
            comp.setId(newId);
            PDAMod.LOGGER.debug("Remapped component ID {} -> {}", oldId, newId);
        }
    }

    private void onOpenApp(final CPacketOpenApp packet, final NetworkEvent.Context context) {
        final var playerId = packet.getPlayerId();
        final var sessionId = packet.getSessionId();
        final var session = ClientSessionHandler.INSTANCE.findById(sessionId);
        if (session == null) {
            PDAMod.LOGGER.warn("Could not find active session {}", sessionId);
            return;
        }
        final var launcher = (ClientLauncher) session.getLauncher();
        final var appType = Objects.requireNonNull(API.getAppTypeRegistry().getValue(packet.getName()));
        final var mappings = packet.getNewIds();
        // If this is the local player, we are waiting for the app to open
        if (isLocalPlayer(playerId)) {
            final var app = session.getLauncher().getOpenApp(appType);
            if (app == null) {
                PDAMod.LOGGER.warn("Could not find open app {}", appType.getName());
                return;
            }
            for (final var view : app.getViews()) {
                remapComponentIds(view.getContainer(), mappings.get(view.getName()));
            }
            launcher.addPendingApp(app);
            return;
        }
        // Otherwise we create an app instance lazily for external sessions
        final var app = launcher.openNow(appType);
        for (final var view : app.getViews()) {
            remapComponentIds(view.getContainer(), mappings.get(view.getName()));
        }
        app.init(session); // Init after remapping as openNow leaves app uninitialized
        launcher.addOpenApp(app);
    }

    private void onTerminateSession(final CPacketTerminateSession packet, final NetworkEvent.Context context) {
        final var playerId = packet.getPlayerId();
        if (isLocalPlayer(playerId)) {
            ClientSessionHandler.INSTANCE.addTerminatedSession(packet.getSessionId());
            return;
        }
        final var extPlayer = getPlayerById(playerId);
        if (extPlayer == null) {
            PDAMod.LOGGER.warn("Could not find player with ID {}", playerId);
            return;
        }
        ClientSessionHandler.INSTANCE.removeActiveSession(packet.getSessionId());
    }

    private void onCloseApp(final CPacketCloseApp packet, final NetworkEvent.Context context) {
        final var playerId = packet.getPlayerId();
        final var sessionId = packet.getSessionId();
        final var session = ClientSessionHandler.INSTANCE.findById(sessionId);
        if (session == null) {
            PDAMod.LOGGER.warn("Could not find active session {}", sessionId);
            return;
        }
        final var launcher = (ClientLauncher) session.getLauncher();
        final var type = Objects.requireNonNull(API.getAppTypeRegistry().getValue(packet.getName()));
        final var app = launcher.getOpenApp(type);
        if (app == null) {
            PDAMod.LOGGER.warn("No open app of type {} found for session {}", type.getName(), sessionId);
            return;
        }
        if (isLocalPlayer(playerId)) {
            launcher.addTerminatedApp(app);
            return;
        }
        launcher.removeOpenApp(app);
    }

    private void onSyncValues(final CPacketSyncValues packet, final NetworkEvent.Context context) {
        // TODO: ...
    }
}
