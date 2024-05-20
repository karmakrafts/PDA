/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.theme.font;

import net.minecraft.resources.ResourceLocation;

/**
 * @author Alexander Hinze
 * @since 20/05/2024
 */
public final class DefaultFontVariant implements FontVariant {
    private final Font font;
    private final FontStyle style;
    private final float size;

    public DefaultFontVariant(final Font font, final FontStyle style, final float size) {
        this.font = font;
        this.style = style;
        this.size = size;
    }

    @Override
    public FontFamily getFamily() {
        return font.getFamily();
    }

    @Override
    public FontCharSet getSupportedChars() {
        return font.getSupportedChars();
    }

    @Override
    public ResourceLocation getLocation() {
        return font.getLocation();
    }

    @Override
    public FontVariant getDefaultVariant() {
        return font.getDefaultVariant();
    }

    @Override
    public FontStyle getStyle() {
        return style;
    }

    @Override
    public float getSize() {
        return size;
    }

    @Override
    public FontVariant derive(final FontStyle style) {
        return font.getFamily().getFont(style, size);
    }

    @Override
    public FontVariant derive(final float size) {
        return font.getFamily().getFont(style, size);
    }
}
