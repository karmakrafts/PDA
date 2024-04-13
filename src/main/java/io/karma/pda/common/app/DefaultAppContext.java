/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.app;

import io.karma.pda.api.common.app.AppContext;
import io.karma.pda.api.common.app.view.AppView;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
public final class DefaultAppContext implements AppContext {
    @Override
    public void addView(final String name, final AppView view) {

    }

    @Override
    public @Nullable AppView removeView(final String name) {
        return null;
    }

    @Override
    public void setFullscreen(final boolean isFullscreen) {

    }

    @Override
    public void showControls() {

    }

    @Override
    public void close() {

    }

    @Override
    public void suspend() {

    }
}
