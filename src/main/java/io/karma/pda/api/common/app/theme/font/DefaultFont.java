/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.theme.font;

import net.minecraft.resources.ResourceLocation;

/**
 * @author Alexander Hinze
 * @since 28/04/2024
 */
public final class DefaultFont implements Font {
    private final FontFamily family;
    private final FontCharSet supportedChars;
    private final ResourceLocation location;
    private final DefaultFontVariant defaultVariant;

    public DefaultFont(final FontFamily family, final FontCharSet supportedChars, final ResourceLocation location) {
        this.family = family;
        this.supportedChars = supportedChars;
        this.location = location;
        defaultVariant = new DefaultFontVariant(this, FontStyle.REGULAR, FontVariant.DEFAULT_SIZE);
    }

    @Override
    public FontVariant getDefaultVariant() {
        return defaultVariant;
    }

    @Override
    public FontCharSet getSupportedChars() {
        return supportedChars;
    }

    @Override
    public FontFamily getFamily() {
        return family;
    }

    @Override
    public ResourceLocation getLocation() {
        return location;
    }
}
