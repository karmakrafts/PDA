/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.app;

import io.karma.pda.api.common.app.App;
import io.karma.pda.api.common.app.AppType;
import io.karma.pda.api.common.app.Launcher;
import io.karma.pda.api.common.app.component.Component;
import io.karma.pda.api.common.app.component.Container;
import io.karma.pda.api.common.session.Session;
import io.karma.pda.api.common.util.Exceptions;
import io.karma.pda.api.common.util.LogMarkers;
import io.karma.pda.common.PDAMod;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;

/**
 * @author Alexander Hinze
 * @since 14/04/2024
 */
public class DefaultLauncher implements Launcher {
    protected final Session session;
    protected final Stack<App> appStack = new Stack<>();
    protected final Object appStackLock = new Object();

    public DefaultLauncher(final Session session) {
        this.session = session;
    }

    protected void tryCompose(final App app) {
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

    protected void tryInit(final App app) {
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

    @ApiStatus.Internal
    public <A extends App> A openNow(final AppType<A> type) {
        final var app = type.create();
        tryCompose(app);
        synchronized (appStackLock) {
            appStack.push(app); // Push app when composed initially
        }
        registerSyncedFields(app);
        return app;
    }

    @ApiStatus.Internal
    public void addOpenApp(final App app) {
        synchronized (appStackLock) {
            appStack.push(app);
        }
    }

    @ApiStatus.Internal
    public void removeOpenApp(final App app) {
        synchronized (appStackLock) {
            appStack.remove(app);
        }
    }

    protected void registerSyncedComponents(final Component component) {
        final var stateHandler = session.getStateHandler();
        stateHandler.register(component);
        if (component instanceof Container container) {
            for (final var child : container.getChildren()) {
                registerSyncedComponents(child);
            }
        }
    }

    protected void registerSyncedFields(final App app) {
        final var stateHandler = session.getStateHandler();
        final var appOwnerName = String.format("%s:%s", session.getId(), app.getType().getName());
        stateHandler.register(appOwnerName, app);
        for (final var view : app.getViews()) {
            final var viewOwnerName = String.format("%s:%s", appOwnerName, view.getName());
            stateHandler.register(viewOwnerName, view);
            registerSyncedComponents(view.getContainer());
        }
    }

    protected void unregisterSyncedComponents(final Component component) {
        final var stateHandler = session.getStateHandler();
        stateHandler.unregister(component);
        if (component instanceof Container container) {
            for (final var child : container.getChildren()) {
                unregisterSyncedComponents(child);
            }
        }
    }

    protected void unregisterSyncedFields(final App app) {
        final var stateHandler = session.getStateHandler();
        final var appOwnerName = String.format("%s:%s", session.getId(), app.getType().getName());
        stateHandler.unregister(appOwnerName, app);
        for (final var view : app.getViews()) {
            final var viewOwnerName = String.format("%s:%s", appOwnerName, view.getName());
            stateHandler.unregister(viewOwnerName, view);
            unregisterSyncedComponents(view.getContainer());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends App> CompletableFuture<@Nullable A> closeApp(final AppType<A> type) {
        synchronized (appStackLock) {
            if (appStack.isEmpty()) {
                return CompletableFuture.completedFuture(null);
            }
            PDAMod.LOGGER.debug(LogMarkers.PROTOCOL, "Closing app {}", type.getName());
            App toRemove = null;
            for (final var app : appStack) {
                if (app.getType() != type) {
                    continue;
                }
                toRemove = app;
                break;
            }
            if (toRemove == null) {
                return CompletableFuture.completedFuture(null);
            }
            unregisterSyncedFields(toRemove);
            toRemove.dispose();
            appStack.remove(toRemove);
            return CompletableFuture.completedFuture((A) toRemove);
        }
    }

    @Override
    public <A extends App> CompletableFuture<@Nullable A> openApp(final AppType<A> type) {
        synchronized (appStackLock) {
            for (final var app : appStack) {
                if (app.getType() != type) {
                    continue;
                }
                return CompletableFuture.completedFuture(null);
            }
            PDAMod.LOGGER.debug(LogMarkers.PROTOCOL, "Opening app {}", type.getName());
            final var app = type.create();
            tryCompose(app);
            tryInit(app);
            registerSyncedFields(app);
            appStack.push(app);
            return CompletableFuture.completedFuture(app);
        }
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
