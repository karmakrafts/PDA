/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
public final class AppState {
    private boolean isFullscreen;
    private boolean areControlsVisible;
    private boolean isSuspended;
    private boolean autoHideControls;

    @JsonCreator
    public AppState(final boolean isFullscreen, final boolean areControlsVisible, final boolean isSuspended,
                    final boolean autoHideControls) {
        this.isFullscreen = isFullscreen;
        this.areControlsVisible = areControlsVisible;
        this.isSuspended = isSuspended;
        this.autoHideControls = autoHideControls;
    }

    @JsonIgnore
    public AppState() {
        this(false, true, false, false);
    }

    @JsonIgnore
    public void setAutoHideControls(boolean autoHideControls) {
        this.autoHideControls = autoHideControls;
    }

    @JsonIgnore
    public void setFullscreen(final boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
    }

    @JsonIgnore
    public void showControls() {
        areControlsVisible = true;
    }

    @JsonIgnore
    public void hideControls() {
        areControlsVisible = false;
    }

    @JsonIgnore
    public void close() {

    }

    @JsonIgnore
    public void suspend() {
        isSuspended = true;
    }

    @JsonGetter
    public boolean isSuspended() {
        return isSuspended;
    }

    @JsonGetter
    public boolean areControlsVisible() {
        return areControlsVisible;
    }

    @JsonGetter
    public boolean isFullscreen() {
        return isFullscreen;
    }

    @JsonGetter
    public boolean autoHideControls() {
        return autoHideControls;
    }
}
