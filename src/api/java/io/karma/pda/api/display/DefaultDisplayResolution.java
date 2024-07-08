/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.display;

/**
 * @author Alexander Hinze
 * @since 02/06/2024
 */
public enum DefaultDisplayResolution implements DisplayResolution {
    // @formatter:off
    SD (256, 288),
    HD (512, 576),
    UHD(1024, 1152);
    // @formatter:on

    private final int width;
    private final int height;

    DefaultDisplayResolution(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public String getResolutionString() {
        return String.format("%dx%d", width, height);
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
