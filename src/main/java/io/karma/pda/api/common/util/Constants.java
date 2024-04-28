/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.util;

import net.minecraft.resources.ResourceLocation;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public final class Constants {
    public static final String MODID = "pda";
    public static final String PROTOCOL_VERSION = "1";
    public static final ResourceLocation COMPONENT_REGISTRY_NAME = new ResourceLocation(MODID, "components");
    public static final ResourceLocation APP_REGISTRY_NAME = new ResourceLocation(MODID, "apps");
    public static final ResourceLocation THEME_REGISTRY_NAME = new ResourceLocation(MODID, "themes");
    public static final ResourceLocation FONT_FAMILY_REGISTRY_NAME = new ResourceLocation(MODID, "font_families");

    // @formatter:off
    private Constants() {}
    // @formatter:on
}
