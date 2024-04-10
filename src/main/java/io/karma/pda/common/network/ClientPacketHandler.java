/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network;

import io.karma.pda.client.session.ClientSessionHandler;
import io.karma.pda.common.network.cb.CPacketCreateSession;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.ApiStatus;

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
    public void setup() {
        registerPacket(PacketIDs.CB_CREATE_SESSION,
            CPacketCreateSession.class,
            CPacketCreateSession::encode,
            CPacketCreateSession::decode,
            this::handleCPacketCreateSession);
    }

    private void handleCPacketCreateSession(final CPacketCreateSession packet, final NetworkEvent.Context context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
            () -> () -> ClientSessionHandler.INSTANCE.addNewSessionId(packet.getRequestId(), packet.getSessionId()));
    }
}
