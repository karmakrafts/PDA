/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.display;

import io.karma.pda.api.common.util.Constants;
import net.minecraft.client.resources.language.I18n;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 13/03/2024
 */
public enum DefaultDisplayType implements DisplayType {
    // @formatter:off
    BW_LCD,
    SRGB_LCD,
    OLED;
    // @formatter:on

    private final Supplier<String> translationSupplier;

    DefaultDisplayType() {
        translationSupplier = () -> I18n.get(String.format("display_type.%s.%s", Constants.MODID, this));
    }

    @Override
    public String getTranslatedName() {
        return translationSupplier.get();
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}