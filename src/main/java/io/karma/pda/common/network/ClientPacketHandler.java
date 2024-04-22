/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network;

import io.karma.pda.api.common.API;
import io.karma.pda.client.app.ClientLauncher;
import io.karma.pda.client.session.ClientSessionHandler;
import io.karma.pda.common.network.cb.*;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

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
        registerPacket(PacketIDs.CB_ADD_SYNC_VALUE,
            CPacketAddSyncValue.class, CPacketAddSyncValue::encode, CPacketAddSyncValue::decode,
            this::onAddSyncValue);
        registerPacket(PacketIDs.CB_REMOVE_SYNC_VALUE,
            CPacketRemoveSyncValue.class, CPacketRemoveSyncValue::encode, CPacketRemoveSyncValue::decode,
            this::onRemoveSyncValue);
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

    private void onOpenApp(final CPacketOpenApp packet, final NetworkEvent.Context context) {
        final var playerId = packet.getPlayerId();
        if (playerId == null) {
            final var session = ClientSessionHandler.INSTANCE.findById(packet.getSessionId());
            if (session == null) {
                return; // TODO: warn?
            }
            final var app = Objects.requireNonNull(API.getAppTypeRegistry().getValue(packet.getName())).create();
            app.clearViews(); // We reconstruct the views from packet data
            for (final var view : packet.getViews()) {
                app.addView(view.getName(), view);
            }
            app.init();
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

    private void onAddSyncValue(final CPacketAddSyncValue packet, final NetworkEvent.Context context) {
        // TODO: ...
    }

    private void onRemoveSyncValue(final CPacketRemoveSyncValue packet, final NetworkEvent.Context context) {
        // TODO: ...
    }

    private void onSyncValues(final CPacketSyncValues packet, final NetworkEvent.Context context) {
        // TODO: ...
    }
}
