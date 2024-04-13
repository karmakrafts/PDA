/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.theme;

import io.karma.material.dynamiccolor.DynamicScheme;
import io.karma.pda.api.common.theme.Themes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DarkTheme implements Theme {
    @Override
    public String getName() {
        return Themes.DARK;
    }

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
