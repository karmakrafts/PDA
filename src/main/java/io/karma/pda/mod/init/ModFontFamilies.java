/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.init;

import io.karma.pda.api.util.Constants;
import io.karma.pda.mod.PDAMod;
import io.karma.peregrine.api.Peregrine;
import net.minecraft.resources.ResourceLocation;

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
        register("fixedsys");
        register("noto_sans");
    }

    private static void register(final String name) {
        PDAMod.FONT_FAMILIES.register(name,
            () -> Peregrine.getFontFamilyFactory().apply(new ResourceLocation(Constants.MODID, name)));
    }
}
