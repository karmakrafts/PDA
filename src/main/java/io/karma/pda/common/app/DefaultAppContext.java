/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.app;

import io.karma.pda.api.common.app.AppContext;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
public final class DefaultAppContext implements AppContext {
    private boolean isFullscreen;
    private boolean areControlsVisible;
    private boolean isSuspended;
    private boolean autoHideControls;

    @Override
    public void setAutoHideControls(boolean autoHideControls) {
        this.autoHideControls = autoHideControls;
    }

    @Override
    public void setFullscreen(final boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
    }

    @Override
    public void showControls() {
        areControlsVisible = true;
    }

    @Override
    public void hideControls() {
        areControlsVisible = false;
    }

    @Override
    public void close() {

    }

    @Override
    public void suspend() {
        isSuspended = true;
    }

    @Override
    public boolean isSuspended() {
        return isSuspended;
    }

    @Override
    public boolean areControlsVisible() {
        return areControlsVisible;
    }

    @Override
    public boolean isFullscreen() {
        return isFullscreen;
    }

    @Override
    public boolean autoHideControls() {
        return autoHideControls;
    }
}
