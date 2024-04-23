/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.app;

import io.karma.pda.api.common.app.App;
import io.karma.pda.api.common.app.AppType;
import io.karma.pda.api.common.app.Launcher;
import io.karma.pda.api.common.app.LauncherSettings;
import io.karma.pda.api.common.session.Session;
import io.karma.pda.api.common.util.LogMarkers;
import io.karma.pda.common.PDAMod;
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
    protected final LauncherSettings settings = new LauncherSettings();

    public DefaultLauncher(final Session session) {
        this.session = session;
        session.getSynchronizer().register(settings);
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
            app.compose();
            app.init();
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

    @Override
    public LauncherSettings getSettings() {
        return settings;
    }
}
