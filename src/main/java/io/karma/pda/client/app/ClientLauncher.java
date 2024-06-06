/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.app;

import io.karma.pda.api.app.App;
import io.karma.pda.api.app.AppType;
import io.karma.pda.api.app.component.Component;
import io.karma.pda.api.app.component.Container;
import io.karma.pda.api.session.Session;
import io.karma.pda.api.util.LogMarkers;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.app.DefaultLauncher;
import io.karma.pda.common.network.sb.SPacketCloseApp;
import io.karma.pda.common.network.sb.SPacketOpenApp;
import io.karma.pda.common.util.BlockingHashMap;
import io.karma.pda.common.util.TreeGraph;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
            .thenApply(app -> {
                if(app == null) {
                    PDAMod.LOGGER.warn("Server didn't respond in time to close app {}, ignoring", name);
                    return null;
                }
                unregisterSyncedFields(app);
                synchronized (appStackLock) {
                    appStack.remove(app);
                }
                app.dispose();
                PDAMod.LOGGER.debug("Closed app {}", name);
                return (A) app;
            });
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
        final var game = Minecraft.getInstance();
        final var app = type.create();
        tryCompose(app);
        synchronized (appStackLock) {
            appStack.push(app); // Push app when composed initially
        }
        // @formatter:off
        final var future = pendingApps.removeLater(name, 30, TimeUnit.SECONDS, PDAMod.EXECUTOR_SERVICE)
            .thenApply(theApp -> {
                if(theApp == null) {
                    synchronized (appStackLock) {
                        appStack.remove(appStack.stream()
                            .filter(a -> a.getType() == type)
                            .findFirst()
                            .orElseThrow());
                    }
                    PDAMod.LOGGER.error("Server didn't respond in time to open app {}, ignoring", name);
                    return null;
                }
                registerSyncedFields(theApp);
                tryInit(theApp);
                return (A)theApp;
            });
        // @formatter:on
        game.execute(() -> {
            final var sessionId = session.getId();
            PDAMod.LOGGER.debug(LogMarkers.PROTOCOL, "Requesting app {} to open for session {}", name, sessionId);
            synchronized (appStackLock) {
                // @formatter:off
                PDAMod.CHANNEL.sendToServer(new SPacketOpenApp(sessionId, name, appStack.stream()
                    .filter(a -> a.getType() == type)
                    .findFirst()
                    .orElseThrow()
                    .getViews()
                    .stream()
                    .map(view -> Pair.of(view.getName(),
                        TreeGraph.from(view.getContainer(), Container.class, Container::getChildren, Component::getId).flatten()))
                    .collect(Collectors.toMap(Pair::getLeft, Pair::getRight))));
                // @formatter:on
            }
        });
        return future;
    }
}
