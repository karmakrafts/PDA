/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.app;

import io.karma.pda.api.app.App;
import io.karma.pda.api.app.AppType;
import io.karma.pda.api.app.Launcher;
import io.karma.pda.api.app.component.Component;
import io.karma.pda.api.app.component.Container;
import io.karma.pda.api.session.Session;
import io.karma.pda.api.util.Exceptions;
import io.karma.pda.mod.PDAMod;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;

/**
 * @author Alexander Hinze
 * @since 14/04/2024
 */
public final class DefaultLauncher implements Launcher {
    private final Session session;
    private final Stack<App> appStack = new Stack<>();
    private final Object appStackLock = new Object();

    public DefaultLauncher(final Session session) {
        this.session = session;
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

    @SuppressWarnings("unchecked")
    @Override
    public <A extends App> CompletableFuture<@Nullable A> closeApp(final AppType<A> type) {
        synchronized (appStackLock) {
            if (appStack.isEmpty()) {
                return CompletableFuture.completedFuture(null);
            }
            PDAMod.LOGGER.debug("Closing app {}", type.getName());
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
            PDAMod.LOGGER.debug("Opening app {}", type.getName());
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
