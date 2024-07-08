/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.display;

/**
 * @author Alexander Hinze
 * @since 05/06/2024
 */
public record DisplayModeSpec(String name, DisplayResolution resolution, DisplayType type) {
    @SuppressWarnings("all")
    private static final DisplayModeSpec NULL = new DisplayModeSpec(null, null, null);

    public DisplayModeSpec(final DisplayResolution resolution, final DisplayType type) {
        this(String.format("%s_%s", resolution, type), resolution, type);
    }

    @SuppressWarnings("all")
    public static DisplayModeSpec nullType() {
        return NULL;
    }

    @Override
    public String toString() {
        return String.format("%s_%s", resolution, type);
    }
}
