/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.app;

import io.karma.pda.api.common.app.App;
import io.karma.pda.api.common.app.AppType;
import io.karma.pda.api.common.app.Launcher;
import io.karma.pda.api.common.session.Session;
import io.karma.pda.common.PDAMod;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;

/**
 * @author Alexander Hinze
 * @since 14/04/2024
 */
public class DefaultLauncher implements Launcher {
    protected final Session session;
    protected final Stack<AppInstance> appStack = new Stack<>();
    protected final Object appStackLock = new Object();

    public DefaultLauncher(final Session session) {
        this.session = session;
    }

    protected record AppInstance(App app, DefaultAppContext context) {
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends App> @Nullable A closeApp(final AppType<A> type) {
        synchronized (appStackLock) {
            if (appStack.isEmpty()) {
                return null;
            }
            PDAMod.LOGGER.debug("Closing app {}", type.getName());
            AppInstance toRemove = null;
            for (final var instance : appStack) {
                if (instance.app.getType() != type) {
                    continue;
                }
                toRemove = instance;
                break;
            }
            if (toRemove == null) {
                return null;
            }
            toRemove.app.dispose(toRemove.context);
            appStack.remove(toRemove);
            return (A) toRemove.app;
        }
    }

    @Override
    public <A extends App> CompletableFuture<@Nullable A> openApp(final AppType<A> type) {
        synchronized (appStackLock) {
            for (final var instance : appStack) {
                if (instance.app.getType() != type) {
                    continue;
                }
                return CompletableFuture.completedFuture(null);
            }
            PDAMod.LOGGER.debug("Opening app {}", type.getName());
            final var app = type.create();
            final var context = new DefaultAppContext();
            app.init(context);
            appStack.push(new AppInstance(app, context));
            return CompletableFuture.completedFuture(app);
        }
    }

    @Override
    public List<App> getActiveApps() {
        synchronized (appStackLock) {
            return appStack.stream().map(AppInstance::app).toList();
        }
    }
}
