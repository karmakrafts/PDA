/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
public interface AppContext {
    void close();

    void setFullscreen(final boolean isFullscreen);

    void showControls();
}
