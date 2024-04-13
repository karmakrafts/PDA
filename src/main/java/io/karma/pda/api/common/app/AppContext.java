/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app;

import io.karma.pda.api.common.app.view.AppView;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
public interface AppContext {
    void addView(final String name, final AppView view);

    @Nullable
    AppView removeView(final String name);

    void setFullscreen(final boolean isFullscreen);

    void showControls();

    void close();

    void suspend();
}
