/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.theme;

import io.karma.material.dynamiccolor.DynamicScheme;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
public final class DarkTheme implements Theme {
    @Override
    public DynamicScheme getScheme() {
        return null;
    }

    @Override
    public Typography getTypography() {
        return null;
    }

    @Override
    public boolean isDark() {
        return false;
    }
}
