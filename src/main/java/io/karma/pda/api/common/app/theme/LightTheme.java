/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.theme;

import io.karma.pda.api.common.util.Color;
import net.minecraft.resources.ResourceLocation;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public class LightTheme extends DynamicTheme {
    public LightTheme(final ResourceLocation name, final Color color) {
        super(name, () -> color, () -> false);
    }
}
