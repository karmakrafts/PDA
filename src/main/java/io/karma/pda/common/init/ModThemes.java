/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.init;

import io.karma.pda.api.common.app.theme.DarkTheme;
import io.karma.pda.api.common.app.theme.LightTheme;
import io.karma.pda.api.common.app.theme.Theme;
import io.karma.pda.api.common.util.Constants;
import io.karma.pda.common.PDAMod;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public final class ModThemes {
    // @formatter:off
    private ModThemes() {}
    // @formatter:on

    public static void register() {
        PDAMod.LOGGER.info("Registering themes");
        register("default_dark", DarkTheme::new);
        register("default_light", LightTheme::new);
    }

    private static void register(final String name, final Function<ResourceLocation, Theme> factory) {
        PDAMod.THEMES.register(name, () -> factory.apply(new ResourceLocation(Constants.MODID, name)));
    }
}
