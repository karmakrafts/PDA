package io.karma.pda.api.util;

import net.minecraft.resources.ResourceLocation;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public final class Constants {
    public static final String MODID = "pda";
    public static final String PROTOCOL_VERSION = "1";
    public static final ResourceLocation APP_REGISTRY_NAME = new ResourceLocation(MODID, "apps");

    // @formatter:off
    private Constants() {}
    // @formatter:on
}
