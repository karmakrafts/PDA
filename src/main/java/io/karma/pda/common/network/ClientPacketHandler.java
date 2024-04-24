/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network;

import io.karma.pda.api.common.API;
import io.karma.pda.api.common.app.component.Component;
import io.karma.pda.api.common.app.component.Container;
import io.karma.pda.client.app.ClientLauncher;
import io.karma.pda.client.session.ClientSessionHandler;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.network.cb.*;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
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
    } // @formatter:on

    private void onCreateSession(final CPacketCreateSession packet, final NetworkEvent.Context context) {
        final var sessionHandler = ClientSessionHandler.INSTANCE;
        final var playerId = packet.getPlayerId();
        final var sessionId = packet.getSessionId();
        if (playerId == null) {
            sessionHandler.addPendingSession(packet.getRequestId(), sessionId);
        }
        // TODO: implement external sessions
    }

    private static void remapIds(final Component component, final Map<UUID, UUID> ids) {
        final var oldId = component.getId();
        final var newId = ids.get(oldId);
        component.setId(newId);
        PDAMod.LOGGER.debug("Remapped component ID {} -> {}", oldId, newId);
        if (component instanceof Container container) {
            for (final var child : container.getChildren()) {
                remapIds(child, ids);
            }
        }
    }

    private void onOpenApp(final CPacketOpenApp packet, final NetworkEvent.Context context) {
        final var playerId = packet.getPlayerId();
        if (playerId == null) {
            final var session = ClientSessionHandler.INSTANCE.findById(packet.getSessionId());
            if (session == null) {
                return; // TODO: warn?
            }

            final var appType = API.getAppTypeRegistry().getValue(packet.getName());
            final var app = session.getLauncher().getOpenApp(appType);
            if (app == null) {
                return; // TODO: warn?
            }

            // Update all component IDs accordingly
            final var mappings = packet.getNewIds();
            for (final var view : app.getViews()) {
                remapIds(view.getContainer(), mappings);
            }

            ((ClientLauncher) session.getLauncher()).addPendingApp(app);
        }
        // TODO: implement external sessions
    }

    private void onTerminateSession(final CPacketTerminateSession packet, final NetworkEvent.Context context) {
        final var playerId = packet.getPlayerId();
        if (playerId == null) {
            ClientSessionHandler.INSTANCE.addTerminatedSession(packet.getSessionId());
        }
        // TODO: implement external sessions
    }

    private void onCloseApp(final CPacketCloseApp packet, final NetworkEvent.Context context) {
        final var playerId = packet.getPlayerId();
        if (playerId == null) {
            final var session = ClientSessionHandler.INSTANCE.findById(packet.getSessionId());
            if (session == null) {
                return; // TODO: warn?
            }
            final var launcher = (ClientLauncher) session.getLauncher();
            final var type = API.getAppTypeRegistry().getValue(packet.getName());
            final var app = launcher.getOpenApp(type);
            if (app == null) {
                return; // TODO: warn?
            }
            launcher.addTerminatedApp(app);
        }
        // TODO: implement external sessions
    }

    private void onSyncValues(final CPacketSyncValues packet, final NetworkEvent.Context context) {
        // TODO: ...
    }
}
