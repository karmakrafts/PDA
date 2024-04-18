/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network;

import io.karma.pda.api.common.API;
import io.karma.pda.client.app.ClientLauncher;
import io.karma.pda.client.session.ClientSessionHandler;
import io.karma.pda.common.network.cb.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
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
    public void registerPackets() {
        registerPacket(PacketIDs.CB_CREATE_SESSION,
            CPacketCreateSession.class,
            CPacketCreateSession::encode,
            CPacketCreateSession::decode,
            this::handleCPacketCreateSession);
        registerPacket(PacketIDs.CB_TERMINATE_SESSION,
            CPacketTerminateSession.class,
            CPacketTerminateSession::encode,
            CPacketTerminateSession::decode,
            this::handleCPacketTerminateSession);
        registerPacket(PacketIDs.CB_SYNC_VALUES,
            CPacketSyncValues.class,
            CPacketSyncValues::encode,
            CPacketSyncValues::decode,
            this::handleCPacketSyncValues);
        registerPacket(PacketIDs.CB_OPEN_APP,
            CPacketOpenApp.class,
            CPacketOpenApp::encode,
            CPacketOpenApp::decode,
            this::handleCPacketOpenApp);
        registerPacket(PacketIDs.CB_CLOSE_APP,
            CPacketCloseApp.class,
            CPacketCloseApp::encode,
            CPacketCloseApp::decode,
            this::handleCPacketCloseApp);
        registerPacket(PacketIDs.CB_UPDATE_APP_STATE,
            CPacketUpdateAppState.class,
            CPacketUpdateAppState::encode,
            CPacketUpdateAppState::decode,
            this::handleCPacketUpdateAppState);
    }

    private void handleCPacketCreateSession(final CPacketCreateSession packet, final NetworkEvent.Context context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
            () -> () -> ClientSessionHandler.INSTANCE.addPendingSession(packet.getRequestId(), packet.getSessionId()));
    }

    private void handleCPacketSyncValues(final CPacketSyncValues packet, final NetworkEvent.Context context) {

    }

    private void handleCPacketOpenApp(final CPacketOpenApp packet, final NetworkEvent.Context context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            final var session = ClientSessionHandler.INSTANCE.getActiveSession(packet.getSessionId());
            if (session == null) {
                return; // TODO: warn?
            }
            final var app = Objects.requireNonNull(API.getAppTypeRegistry().getValue(packet.getName())).create();
            app.clearViews(); // We reconstruct the views from packet data
            for (final var view : packet.getViews()) {
                app.addView(view.getName(), view);
            }
            ((ClientLauncher) session.getLauncher()).addPendingApp(app);
        });
    }

    private void handleCPacketTerminateSession(final CPacketTerminateSession packet,
                                               final NetworkEvent.Context context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
            () -> () -> ClientSessionHandler.INSTANCE.addTerminatedSession(packet.getSessionId()));
    }

    private void handleCPacketCloseApp(final CPacketCloseApp packet, final NetworkEvent.Context context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            final var session = ClientSessionHandler.INSTANCE.getActiveSession(packet.getSessionId());
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
        });
    }

    private void handleCPacketUpdateAppState(final CPacketUpdateAppState packet, final NetworkEvent.Context context) {

    }
}
