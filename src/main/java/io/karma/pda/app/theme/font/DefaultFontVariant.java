/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.app.theme.font;

import io.karma.pda.api.app.theme.font.*;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.resources.ResourceLocation;

/**
 * @author Alexander Hinze
 * @since 20/05/2024
 */
public final class DefaultFontVariant implements FontVariant {
    private final Font font;
    private final FontStyle style;
    private final float size;
    private final Object2FloatOpenHashMap<String> variationAxisOverrides = new Object2FloatOpenHashMap<>();

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
    public FontVariant withStyle(final FontStyle style) {
        return font.getFamily().getFont(style, size);
    }

    @Override
    public FontVariant withSize(final float size) {
        return font.getFamily().getFont(style, size);
    }

    @Override
    public FontVariant withVar(final String name, final float value) {
        final var variant = font.getFamily().getFont(style, size);
        if (!(variant instanceof DefaultFontVariant defaultVariant)) {
            return this;
        }
        defaultVariant.variationAxisOverrides.put(name, value);
        return variant;
    }

    @Override
    public float getVariationAxis(final String name) {
        return variationAxisOverrides.getOrDefault(name, font.getVariationAxis(name));
    }

    @Override
    public Object2FloatMap<String> getVariationAxes() {
        final var axes = new Object2FloatOpenHashMap<>(font.getVariationAxes());
        axes.putAll(variationAxisOverrides);
        return axes;
    }
}
