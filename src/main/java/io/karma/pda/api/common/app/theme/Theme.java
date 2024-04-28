/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.theme;

import io.karma.material.dynamiccolor.DynamicScheme;
import io.karma.pda.api.common.app.theme.font.FontSet;
import net.minecraft.resources.ResourceLocation;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
public interface Theme {
    ResourceLocation getName();

    DynamicScheme getScheme();

    FontSet getFontSet();

    // Helper for declaring registry object holder fields
    static Theme nullType() {
        return null;
    }
}
