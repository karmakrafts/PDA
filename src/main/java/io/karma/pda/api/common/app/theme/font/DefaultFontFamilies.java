/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.theme.font;

import io.karma.pda.api.common.util.Constants;
import net.minecraftforge.registries.ObjectHolder;

/**
 * @author Alexander Hinze
 * @since 05/05/2024
 */
public final class DefaultFontFamilies {
    public static final String REGISTRY_NAME = Constants.MODID + ":font_families";

    @ObjectHolder(value = Constants.MODID + ":fixedsys", registryName = REGISTRY_NAME)
    public static final FontFamily FIXEDSYS = FontFamily.nullType();
    @ObjectHolder(value = Constants.MODID + ":figtree", registryName = REGISTRY_NAME)
    public static final FontFamily FIGTREE = FontFamily.nullType();

    // @formatter:off
    private DefaultFontFamilies() {}
    // @formatter:on
}
