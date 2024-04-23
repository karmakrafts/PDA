/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public interface Launcher {
    <A extends App> CompletableFuture<@Nullable A> openApp(final AppType<A> type);

    <A extends App> CompletableFuture<@Nullable A> closeApp(final AppType<A> type);

    @Nullable
    App getCurrentApp();

    <A extends App> @Nullable A getOpenApp(final AppType<A> type);

    List<App> getOpenApps();

    LauncherSettings getSettings();

    default CompletableFuture<@Nullable App> closeApp() {
        final var apps = getOpenApps();
        if (apps.isEmpty()) {
            return null;
        }
        return closeApp(apps.get(0).getType()).thenApply(app -> (App) app);
    }

    default List<App> getSuspendedApps() {
        return getOpenApps().stream().filter(app -> app.getState().isSuspended()).toList();
    }
}
