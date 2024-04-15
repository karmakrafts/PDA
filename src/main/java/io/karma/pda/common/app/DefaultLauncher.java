/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.app;

import io.karma.pda.api.common.app.App;
import io.karma.pda.api.common.app.AppType;
import io.karma.pda.api.common.app.Launcher;
import io.karma.sliced.slice.Slice;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * @author Alexander Hinze
 * @since 14/04/2024
 */
public class DefaultLauncher implements Launcher {
    public static final DefaultLauncher INSTANCE = new DefaultLauncher();

    @Override
    public <A extends App> @Nullable A closeApp(final AppType<A> type) {
        throw new RuntimeException("Default launcher does not provide this functionality");
    }

    @Override
    public <A extends App> CompletableFuture<@Nullable A> openApp(AppType<A> type) {
        return CompletableFuture.failedFuture(new RuntimeException(
            "Default launcher does not provide this functionality"));
    }

    @Override
    public Slice<App> getActiveApps() {
        return Slice.empty();
    }
}
