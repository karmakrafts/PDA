/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.init;

import io.karma.pda.api.common.app.theme.font.FontFamily;
import io.karma.pda.api.common.util.Constants;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.app.theme.font.DefaultFontFamily;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

/**
 * @author Alexander Hinze
 * @since 28/04/2024
 */
public final class ModFontFamilies {
    // @formatter:off
    private ModFontFamilies() {}
    // @formatter:on

    public static void register() {
        PDAMod.LOGGER.debug("Registering font families");
        register("fixedsys", DefaultFontFamily::new);
        register("noto_sans", DefaultFontFamily::new);
    }

    private static void register(final String name, final Function<ResourceLocation, FontFamily> factory) {
        PDAMod.FONT_FAMILIES.register(name, () -> factory.apply(new ResourceLocation(Constants.MODID, name)));
    }
}
