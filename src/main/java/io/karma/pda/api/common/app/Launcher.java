/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app;

import io.karma.sliced.slice.Slice;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public interface Launcher {
    <A extends App> CompletableFuture<@Nullable A> openApp(final AppType<A> type);

    Slice<App> getActiveApps();

    @Nullable
    <A extends App> A closeApp(final AppType<A> type);

    default @Nullable App closeApp() {
        final var apps = getActiveApps();
        if (apps.size() == 0) {
            return null;
        }
        return closeApp(apps.get(0).getType());
    }
}
