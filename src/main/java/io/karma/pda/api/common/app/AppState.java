/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
public final class AppState {
    private boolean isFullscreen = false;
    private boolean areControlsVisible = true;
    private boolean isSuspended = false;
    private boolean autoHideControls = false;

    @JsonSetter("is_fullscreen")
    public void setIsFullscreen(final boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
    }

    @JsonSetter("are_controls_visible")
    public void setAreControlsVisible(final boolean areControlsVisible) {
        this.areControlsVisible = areControlsVisible;
    }

    @JsonSetter("is_suspended")
    public void setIsSuspended(final boolean isSuspended) {
        this.isSuspended = isSuspended;
    }

    @JsonSetter("auto_hide_controls")
    public void setAutoHideControls(final boolean autoHideControls) {
        this.autoHideControls = autoHideControls;
    }

    @JsonGetter("is_fullscreen")
    public boolean isFullscreen() {
        return isFullscreen;
    }

    @JsonGetter("are_controls_visible")
    public boolean areControlsVisible() {
        return areControlsVisible;
    }

    @JsonGetter("is_suspended")
    public boolean isSuspended() {
        return isSuspended;
    }

    @JsonGetter("auto_hide_controls")
    public boolean autoHideControls() {
        return autoHideControls;
    }
}
