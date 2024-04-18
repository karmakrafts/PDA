/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app;

import io.karma.pda.api.common.sync.Synced;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
public final class AppState {
    private final Synced<Boolean> isFullscreen = Synced.withInitial(false);
    private final Synced<Boolean> areControlsVisible = Synced.withInitial(true);
    private final Synced<Boolean> isSuspended = Synced.withInitial(false);
    private final Synced<Boolean> autoHideControls = Synced.withInitial(false);

    public void setIsFullscreen(final boolean isFullscreen) {
        this.isFullscreen.set(isFullscreen);
    }

    public void setAreControlsVisible(final boolean areControlsVisible) {
        this.areControlsVisible.set(areControlsVisible);
    }

    public void setIsSuspended(final boolean isSuspended) {
        this.isSuspended.set(isSuspended);
    }

    public void setAutoHideControls(final boolean autoHideControls) {
        this.autoHideControls.set(autoHideControls);
    }

    public boolean isFullscreen() {
        return isFullscreen.get();
    }

    public boolean areControlsVisible() {
        return areControlsVisible.get();
    }

    public boolean isSuspended() {
        return isSuspended.get();
    }

    public boolean autoHideControls() {
        return autoHideControls.get();
    }
}
