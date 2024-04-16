/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network;

import io.karma.pda.api.common.API;
import io.karma.pda.client.app.ClientLauncher;
import io.karma.pda.client.session.ClientSessionHandler;
import io.karma.pda.common.network.cb.CPacketCreateSession;
import io.karma.pda.common.network.cb.CPacketOpenApp;
import io.karma.pda.common.network.cb.CPacketSyncValues;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.function.Supplier;

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
    }

    private void handleCPacketCreateSession(final CPacketCreateSession packet,
                                            final Supplier<NetworkEvent.Context> contextGetter) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
            () -> () -> ClientSessionHandler.INSTANCE.addPendingSession(packet.getRequestId(), packet.getSessionId()));
    }

    private void handleCPacketSyncValues(final CPacketSyncValues packet,
                                         final Supplier<NetworkEvent.Context> contextGetter) {

    }

    private void handleCPacketOpenApp(final CPacketOpenApp packet, final Supplier<NetworkEvent.Context> contextGetter) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            final var session = ClientSessionHandler.INSTANCE.getActiveSession();
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
}
