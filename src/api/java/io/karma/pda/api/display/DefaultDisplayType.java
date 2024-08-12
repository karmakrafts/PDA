/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.display;

import io.karma.pda.api.util.Constants;
import net.minecraft.client.resources.language.I18n;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 13/03/2024
 */
public enum DefaultDisplayType implements DisplayType {
    // @formatter:off
    BW_LCD  (0.025F,    32, 0.05F,  0.06F),
    SRGB_LCD(0.0125F,   32, 0.03F,  0.03F),
    OLED    (0.000625F, 64, 0.001F, 0.005F);
    // @formatter:on

    private final Supplier<String> translationSupplier;
    private final float glitchFactor;
    private final int glitchBlocks;
    private final float glitchRate;
    private final float pixelationFactor;

    DefaultDisplayType(final float glitchFactor, final int glitchBlocks, final float glitchRate,
                       final float pixelationFactor) {
        translationSupplier = () -> I18n.get(String.format("display_type.%s.%s", Constants.MODID, this));
        this.glitchFactor = glitchFactor;
        this.glitchBlocks = glitchBlocks;
        this.glitchRate = glitchRate;
        this.pixelationFactor = pixelationFactor;
    }

    @Override
    public float getGlitchFactor() {
        return glitchFactor;
    }

    @Override
    public int getGlitchBlocks() {
        return glitchBlocks;
    }

    @Override
    public float getGlitchRate() {
        return glitchRate;
    }

    @Override
    public float getPixelationFactor() {
        return pixelationFactor;
    }

    @Override
    public int getIndex() {
        return ordinal();
    }

    @Override
    public String getName() {
        return name().toLowerCase();
    }

    @Override
    public String getTranslatedName() {
        return translationSupplier.get();
    }

    @Override
    public String toString() {
        return getName();
    }
}