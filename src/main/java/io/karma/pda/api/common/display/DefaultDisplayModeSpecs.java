/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.display;

import io.karma.pda.api.common.util.Constants;
import net.minecraftforge.registries.ObjectHolder;

/**
 * @author Alexander Hinze
 * @since 06/06/2024
 */
public final class DefaultDisplayModeSpecs {
    public static final String REGISTRY_NAME = Constants.MODID + ":display_modes";

    @ObjectHolder(value = Constants.MODID + ":sd_bw_lcd", registryName = REGISTRY_NAME)
    public static final DisplayModeSpec SD_BW_LCD = DisplayModeSpec.nullType();
    @ObjectHolder(value = Constants.MODID + ":sd_srgb_lcd", registryName = REGISTRY_NAME)
    public static final DisplayModeSpec SD_SRGB_LCD = DisplayModeSpec.nullType();
    @ObjectHolder(value = Constants.MODID + ":sd_oled", registryName = REGISTRY_NAME)
    public static final DisplayModeSpec SD_OLED = DisplayModeSpec.nullType();

    @ObjectHolder(value = Constants.MODID + ":hd_bw_lcd", registryName = REGISTRY_NAME)
    public static final DisplayModeSpec HD_BW_LCD = DisplayModeSpec.nullType();
    @ObjectHolder(value = Constants.MODID + ":hd_srgb_lcd", registryName = REGISTRY_NAME)
    public static final DisplayModeSpec HD_SRGB_LCD = DisplayModeSpec.nullType();
    @ObjectHolder(value = Constants.MODID + ":hd_oled", registryName = REGISTRY_NAME)
    public static final DisplayModeSpec HD_OLED = DisplayModeSpec.nullType();

    @ObjectHolder(value = Constants.MODID + ":uhd_bw_lcd", registryName = REGISTRY_NAME)
    public static final DisplayModeSpec UHD_BW_LCD = DisplayModeSpec.nullType();
    @ObjectHolder(value = Constants.MODID + ":uhd_srgb_lcd", registryName = REGISTRY_NAME)
    public static final DisplayModeSpec UHD_SRGB_LCD = DisplayModeSpec.nullType();
    @ObjectHolder(value = Constants.MODID + ":uhd_oled", registryName = REGISTRY_NAME)
    public static final DisplayModeSpec UHD_OLED = DisplayModeSpec.nullType();

    // @formatter:off
    private DefaultDisplayModeSpecs() {}
    // @formatter:on
}
