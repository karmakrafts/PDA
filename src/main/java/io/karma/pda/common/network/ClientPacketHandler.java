/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network;

import io.karma.pda.api.common.API;
import io.karma.pda.api.common.app.component.Component;
import io.karma.pda.api.common.app.component.Container;
import io.karma.pda.client.app.ClientLauncher;
import io.karma.pda.client.session.ClientSession;
import io.karma.pda.client.session.ClientSessionHandler;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.network.cb.*;
import io.karma.pda.common.session.DockedSessionContext;
import io.karma.pda.common.session.HandheldSessionContext;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
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
    } // @formatter:on

    private static @Nullable Player getPlayerById(final UUID id) {
        final var level = Minecraft.getInstance().level;
        if (level == null) {
            return null;
        }
        return level.getPlayerByUUID(id);
    }

    private static boolean isLocalPlayer(final @Nullable UUID id) {
        if (id == null) {
            return false;
        }
        final var player = Minecraft.getInstance().player;
        return player != null && player.getUUID().equals(id);
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

    private static void remapComponentIds(final Component component, final Map<UUID, UUID> ids) {
        final var oldId = component.getId();
        final var newId = ids.get(oldId);
        component.setId(newId);
        PDAMod.LOGGER.debug("Remapped component ID {} -> {}", oldId, newId);
        if (component instanceof Container container) {
            for (final var child : container.getChildren()) {
                remapComponentIds(child, ids);
            }
        }
    }

    private void onOpenApp(final CPacketOpenApp packet, final NetworkEvent.Context context) {
        final var playerId = packet.getPlayerId();
        final var sessionId = packet.getSessionId();
        if (isLocalPlayer(playerId)) {
            PDAMod.EXECUTOR_SERVICE.submit(() -> {
                final var session = ClientSessionHandler.INSTANCE.findById(sessionId);
                if (session == null) {
                    PDAMod.LOGGER.warn("Could not find active session {}", sessionId);
                    return;
                }
                final var appType = Objects.requireNonNull(API.getAppTypeRegistry().getValue(packet.getName()));
                final var app = session.getLauncher().getOpenApp(appType);
                if (app == null) {
                    PDAMod.LOGGER.warn("No open app of type {} found for session {}", appType.getName(), sessionId);
                    return;
                }

                // Update all component IDs accordingly
                final var mappings = packet.getNewIds();
                for (final var view : app.getViews()) {
                    remapComponentIds(view.getContainer(), mappings);
                }
                ((ClientLauncher) session.getLauncher()).addPendingApp(app);
            });
        }
        // TODO: implement external sessions
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
        if (isLocalPlayer(playerId)) {
            PDAMod.EXECUTOR_SERVICE.submit(() -> {
                final var session = ClientSessionHandler.INSTANCE.findById(sessionId);
                if (session == null) {
                    PDAMod.LOGGER.warn("Could not find active session {}", sessionId);
                    return;
                }
                final var launcher = (ClientLauncher) session.getLauncher();
                final var type = API.getAppTypeRegistry().getValue(packet.getName());
                final var app = launcher.getOpenApp(type);
                if (app == null) {
                    PDAMod.LOGGER.warn("No open app of type {} found for session {}", type.getName(), sessionId);
                    return;
                }
                launcher.addTerminatedApp(app);
            });
        }
        // TODO: ...
    }

    private void onSyncValues(final CPacketSyncValues packet, final NetworkEvent.Context context) {
        // TODO: ...
    }
}
