/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.display;

/**
 * @author Alexander Hinze
 * @since 05/06/2024
 */
public interface DisplayResolution {
    int getWidth();

    int getHeight();

    String getResolutionString();
}
