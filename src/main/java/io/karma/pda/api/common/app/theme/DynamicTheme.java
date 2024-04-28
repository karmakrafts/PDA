/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.theme;

import io.karma.material.dynamiccolor.DynamicScheme;
import io.karma.material.hct.Hct;
import io.karma.material.scheme.SchemeNeutral;
import io.karma.pda.api.common.app.theme.font.FontSet;
import io.karma.pda.api.common.color.Color;
import net.minecraft.resources.ResourceLocation;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public class DynamicTheme implements Theme {
    private final ResourceLocation name;
    private Color lastColor;
    private boolean lastDarkMode;
    private SchemeNeutral scheme;
    private FontSet fontSet;

    public DynamicTheme(final ResourceLocation name) {
        this.name = name;
    }

    public void update(final Color color, final boolean isDark) {
        if (color.equals(lastColor) && isDark == lastDarkMode) {
            return;
        }
        lastColor = Color.unpackARGB(scheme.sourceColorArgb);
        lastDarkMode = scheme.isDark;
        scheme = new SchemeNeutral(Hct.fromInt(color.packARGB()), isDark, 1.0D);
    }

    @Override
    public FontSet getFontSet() {
        return fontSet;
    }

    @Override
    public ResourceLocation getName() {
        return name;
    }

    @Override
    public DynamicScheme getScheme() {
        return scheme;
    }
}
