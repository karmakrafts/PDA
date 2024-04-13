/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app;

import io.karma.sliced.slice.Slice;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public interface Launcher extends App {
    @Nullable
    App closeApp();

    <A extends App> A openApp(final AppType<A> type);

    Slice<App> getActiveApps();
}
