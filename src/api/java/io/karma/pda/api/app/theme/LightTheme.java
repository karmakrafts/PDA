/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.app.theme;

import io.karma.material.scheme.SchemeVibrant;
import io.karma.pda.api.color.Color;
import net.minecraft.resources.ResourceLocation;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public class LightTheme extends DefaultTheme {
    public LightTheme(final ResourceLocation name) {
        super(name, SchemeVibrant::new, new Color(0, 106, 255), false);
    }
}
