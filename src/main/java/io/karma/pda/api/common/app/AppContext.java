/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
public interface AppContext {
    void setFullscreen(final boolean isFullscreen);

    void setAutoHideControls(final boolean autoHideControls);

    void showControls();

    void hideControls();

    void close();

    void suspend();

    boolean isSuspended();

    boolean areControlsVisible();

    boolean isFullscreen();

    boolean autoHideControls();
}
