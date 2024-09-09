/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.app.theme;

import io.karma.material.dynamiccolor.DynamicScheme;
import io.karma.material.hct.Hct;
import io.karma.peregrine.api.color.Color;
import net.minecraft.resources.ResourceLocation;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public class DefaultTheme implements Theme {
    private final ResourceLocation name;
    private final SchemeFactory<?> schemeFactory;
    private Color lastColor;
    private boolean lastDarkMode;
    private DynamicScheme scheme;

    public DefaultTheme(final ResourceLocation name,
                        final SchemeFactory<?> schemeFactory,
                        final Color accentColor,
                        final boolean isDark) {
        this.name = name;
        this.schemeFactory = schemeFactory;
        update(accentColor, isDark);
    }

    public void update(final Color color, final boolean isDark) {
        if (color.equals(lastColor) && isDark == lastDarkMode) {
            return;
        }
        if (scheme != null) {
            lastColor = Color.unpackARGB(scheme.sourceColorArgb);
            lastDarkMode = scheme.isDark;
        }
        scheme = schemeFactory.create(Hct.fromInt(color.packARGB()), isDark, 1.0D);
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
