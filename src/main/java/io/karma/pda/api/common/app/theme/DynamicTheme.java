/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.theme;

import io.karma.material.dynamiccolor.DynamicScheme;
import io.karma.material.hct.Hct;
import io.karma.material.scheme.SchemeNeutral;
import io.karma.pda.api.common.util.Color;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public class DynamicTheme implements Theme {
    private final ResourceLocation name;
    private final Supplier<Color> colorSupplier;
    private final BooleanSupplier darkModeSupplier;
    private Color lastColor;
    private boolean lastDarkMode;
    private SchemeNeutral scheme;

    public DynamicTheme(final ResourceLocation name, final Supplier<Color> colorSupplier,
                        final BooleanSupplier darkModeSupplier) {
        this.name = name;
        this.colorSupplier = colorSupplier;
        this.darkModeSupplier = darkModeSupplier;
        lastColor = colorSupplier.get();
        lastDarkMode = darkModeSupplier.getAsBoolean();
        updateSchemeIfNeeded();
    }

    private void updateSchemeIfNeeded() {
        final var color = colorSupplier.get();
        final var darkMode = darkModeSupplier.getAsBoolean();
        if (color.equals(lastColor) && darkMode == lastDarkMode) {
            return;
        }
        lastColor = Color.unpackARGB(scheme.sourceColorArgb);
        lastDarkMode = scheme.isDark;
        scheme = new SchemeNeutral(Hct.fromInt(color.packARGB()), darkMode, 1.0D);
    }

    @Override
    public ResourceLocation getName() {
        return name;
    }

    @Override
    public DynamicScheme getScheme() {
        updateSchemeIfNeeded();
        return scheme;
    }
}
