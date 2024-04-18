/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.app;

import io.karma.pda.api.common.app.App;
import io.karma.pda.api.common.app.AppType;
import io.karma.pda.api.common.session.Session;
import io.karma.pda.api.common.util.LogMarkers;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.app.DefaultLauncher;
import io.karma.pda.common.network.sb.SPacketCloseApp;
import io.karma.pda.common.network.sb.SPacketOpenApp;
import io.karma.pda.common.util.BlockingHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Alexander Hinze
 * @since 14/04/2024
 */
@OnlyIn(Dist.CLIENT)
public class ClientLauncher extends DefaultLauncher {
    private final BlockingHashMap<ResourceLocation, App> pendingApps = new BlockingHashMap<>();
    private final BlockingHashMap<ResourceLocation, App> terminatedApps = new BlockingHashMap<>();

    public ClientLauncher(final Session session) {
        super(session);
    }

    @ApiStatus.Internal
    public void addPendingApp(final App app) {
        final var name = app.getType().getName();
        if (pendingApps.containsKey(name)) {
            return;
        }
        pendingApps.put(name, app);
        PDAMod.LOGGER.debug(LogMarkers.PROTOCOL, "Added pending app {}", app.getType().getName());
    }

    @ApiStatus.Internal
    public void addTerminatedApp(final App app) {
        final var name = app.getType().getName();
        if (terminatedApps.containsKey(name)) {
            return;
        }
        terminatedApps.put(name, app);
        PDAMod.LOGGER.debug(LogMarkers.PROTOCOL, "Added terminated app {}", app.getType().getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends App> CompletableFuture<@Nullable A> closeApp(final AppType<A> type) {
        final var name = type.getName();
        // @formatter:off
        final var future = terminatedApps.removeLater(name, 30, TimeUnit.SECONDS, PDAMod.EXECUTOR_SERVICE)
            .thenApply(app -> (A) app);
        // @formatter:on
        Minecraft.getInstance().execute(() -> {
            final var sessionId = session.getId();
            PDAMod.LOGGER.debug(LogMarkers.PROTOCOL, "Requesting topmost app to close for session {}", sessionId);
            PDAMod.CHANNEL.sendToServer(new SPacketCloseApp(sessionId, name));
        });
        return future;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends App> CompletableFuture<@Nullable A> openApp(final AppType<A> type) {
        final var name = type.getName();
        // @formatter:off
        final var future = pendingApps.removeLater(name, 30, TimeUnit.SECONDS, PDAMod.EXECUTOR_SERVICE)
            .thenApply(app -> (A) app);
        // @formatter:on
        Minecraft.getInstance().execute(() -> {
            final var sessionId = session.getId();
            PDAMod.LOGGER.debug(LogMarkers.PROTOCOL, "Requesting app {} to open for session {}", name, sessionId);
            PDAMod.CHANNEL.sendToServer(new SPacketOpenApp(sessionId, name));
        });
        return future;
    }
}
