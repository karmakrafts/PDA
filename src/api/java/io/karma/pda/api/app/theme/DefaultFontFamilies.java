/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.app.theme;

import io.karma.pda.api.util.Constants;
import io.karma.peregrine.api.Peregrine;
import io.karma.peregrine.api.font.FontFamily;
import net.minecraftforge.registries.ObjectHolder;

/**
 * @author Alexander Hinze
 * @since 09/09/2024
 */
public final class DefaultFontFamilies {
    public static final String REGISTRY_NAME = Peregrine.MODID + "font_families";

    @ObjectHolder(registryName = REGISTRY_NAME, value = Constants.MODID + ":noto_sans")
    public static final FontFamily NOTO_SANS = FontFamily.nullType();
    @ObjectHolder(registryName = REGISTRY_NAME, value = Constants.MODID + ":fixedsys")
    public static final FontFamily FIXEDSYS = FontFamily.nullType();

    // @formatter:off
    private DefaultFontFamilies() {}
    // @formatter:on
}
