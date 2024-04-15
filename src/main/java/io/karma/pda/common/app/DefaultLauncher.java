/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.app;

import io.karma.pda.api.common.app.App;
import io.karma.pda.api.common.app.AppType;
import io.karma.pda.api.common.app.Launcher;
import io.karma.pda.api.common.session.Session;
import io.karma.sliced.slice.Slice;
import org.jetbrains.annotations.Nullable;

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

    @SuppressWarnings("unchecked")
    @Override
    public <A extends App> @Nullable A closeApp(final AppType<A> type) {
        synchronized (appStackLock) {
            if (appStack.isEmpty()) {
                return null;
            }
            App toRemove = null;
            for (final var app : appStack) {
                if (app.getType() != type) {
                    continue;
                }
                toRemove = app;
                break;
            }
            appStack.remove(toRemove);
            return (A) toRemove;
        }
    }

    @Override
    public <A extends App> CompletableFuture<@Nullable A> openApp(final AppType<A> type) {
        final var app = type.create();
        appStack.push(app);
        return CompletableFuture.completedFuture(app);
    }

    @Override
    public Slice<App> getActiveApps() {
        return Slice.of(appStack);
    }
}
