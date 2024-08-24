/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.app;

import io.karma.pda.api.app.App;
import io.karma.pda.api.app.AppType;
import io.karma.pda.api.app.Launcher;
import io.karma.pda.api.app.component.Component;
import io.karma.pda.api.app.component.Container;
import io.karma.pda.api.session.Session;
import io.karma.pda.api.util.Exceptions;
import io.karma.pda.mod.PDAMod;
import io.karma.pda.mod.network.sb.SPacketCloseApp;
import io.karma.pda.mod.network.sb.SPacketOpenApp;
import io.karma.pda.mod.util.BlockingHashMap;
import io.karma.pda.mod.util.TreeGraph;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Alexander Hinze
 * @since 14/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ClientLauncher implements Launcher {
    private final BlockingHashMap<ResourceLocation, App> pendingApps = new BlockingHashMap<>();
    private final BlockingHashMap<ResourceLocation, App> terminatedApps = new BlockingHashMap<>();
    private final Session session;
    private final Stack<App> appStack = new Stack<>();
    private final Object appStackLock = new Object();

    public ClientLauncher(final Session session) {
        this.session = session;
    }

    @Internal
    public <A extends App> A openNow(final AppType<A> type) {
        final var app = type.create();
        tryCompose(app);
        synchronized (appStackLock) {
            appStack.push(app); // Push app when composed initially
        }
        registerSyncedFields(app);
        return app;
    }

    @Internal
    public void addOpenApp(final App app) {
        synchronized (appStackLock) {
            appStack.push(app);
        }
    }

    @Internal
    public void removeOpenApp(final App app) {
        synchronized (appStackLock) {
            appStack.remove(app);
        }
    }

    private void tryCompose(final App app) {
        try {
            app.compose();
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error("Composition for {} in session {} failed: {}",
                app.getType().getName(),
                session.getId(),
                Exceptions.toFancyString(error));
        }
    }

    private void tryInit(final App app) {
        try {
            app.init(session);
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error("Initialization for {} in session {} failed: {}",
                app.getType().getName(),
                session.getId(),
                Exceptions.toFancyString(error));
        }
    }

    private void registerSyncedComponents(final Component component) {
        final var stateHandler = session.getStateHandler();
        stateHandler.register(component);
        if (component instanceof Container container) {
            for (final var child : container.getChildren()) {
                registerSyncedComponents(child);
            }
        }
    }

    private void registerSyncedFields(final App app) {
        final var stateHandler = session.getStateHandler();
        final var appOwnerName = String.format("%s:%s", session.getId(), app.getType().getName());
        stateHandler.register(appOwnerName, app);
        for (final var view : app.getViews()) {
            final var viewOwnerName = String.format("%s:%s", appOwnerName, view.getName());
            stateHandler.register(viewOwnerName, view);
            registerSyncedComponents(view.getContainer());
        }
    }

    private void unregisterSyncedComponents(final Component component) {
        final var stateHandler = session.getStateHandler();
        stateHandler.unregister(component);
        if (component instanceof Container container) {
            for (final var child : container.getChildren()) {
                unregisterSyncedComponents(child);
            }
        }
    }

    private void unregisterSyncedFields(final App app) {
        final var stateHandler = session.getStateHandler();
        final var appOwnerName = String.format("%s:%s", session.getId(), app.getType().getName());
        stateHandler.unregister(appOwnerName, app);
        for (final var view : app.getViews()) {
            final var viewOwnerName = String.format("%s:%s", appOwnerName, view.getName());
            stateHandler.unregister(viewOwnerName, view);
            unregisterSyncedComponents(view.getContainer());
        }
    }

    @Internal
    public void addPendingApp(final App app) {
        final var name = app.getType().getName();
        if (pendingApps.containsKey(name)) {
            return;
        }
        pendingApps.put(name, app);
        PDAMod.LOGGER.debug("Added pending app {}", app.getType().getName());
    }

    @Internal
    public void addTerminatedApp(final App app) {
        final var name = app.getType().getName();
        if (terminatedApps.containsKey(name)) {
            return;
        }
        terminatedApps.put(name, app);
        PDAMod.LOGGER.debug("Added terminated app {}", app.getType().getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends App> CompletableFuture<@Nullable A> closeApp(final AppType<A> type) {
        final var name = type.getName();
        // @formatter:off
        final var future = terminatedApps.removeLater(name, 30, TimeUnit.SECONDS, PDAMod.EXECUTOR_SERVICE)
            .exceptionally(exception -> {
                PDAMod.LOGGER.error("Didn't get server response in time", exception);
                return null;
            })
            .thenApply(app -> {
                if(app == null) {
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
            PDAMod.LOGGER.debug("Requesting topmost app to close for session {}", sessionId);
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
            .exceptionally(exception -> {
                synchronized (appStackLock) {
                    appStack.remove(appStack.stream()
                        .filter(a -> a.getType() == type)
                        .findFirst()
                        .orElseThrow());
                }
                PDAMod.LOGGER.error("Server didn't respond in time to open app", exception);
                return null;
            })
            .thenApply(theApp -> {
                if(theApp == null) {
                    return null;
                }
                registerSyncedFields(theApp);
                tryInit(theApp);
                return (A)theApp;
            });
        // @formatter:on
        game.execute(() -> {
            final var sessionId = session.getId();
            PDAMod.LOGGER.debug("Requesting app {} to open for session {}", name, sessionId);
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

    @Override
    public @Nullable App getCurrentApp() {
        synchronized (appStackLock) {
            if (appStack.isEmpty()) {
                return null;
            }
            return appStack.peek();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends App> @Nullable A getOpenApp(final AppType<A> type) {
        synchronized (appStackLock) {
            for (final var app : appStack) {
                if (app.getType() != type) {
                    continue;
                }
                return (A) app;
            }
        }
        return null;
    }

    @Override
    public List<App> getOpenApps() {
        synchronized (appStackLock) {
            return Collections.unmodifiableList(appStack);
        }
    }
}
