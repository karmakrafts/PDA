/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.app.theme;

import io.karma.material.dynamiccolor.DynamicScheme;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
public interface Theme {
    @SuppressWarnings("all")
    @NotNull
    static Theme nullType() {
        return null;
    }

    ResourceLocation getName();

    DynamicScheme getScheme();
}
