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
    private final ResourceLocation location;
    private final FontStyle style;
    private final float size;

    DefaultFont(final FontFamily family, final ResourceLocation location, final FontStyle style, final float size) {
        this.family = family;
        this.location = location;
        this.style = style;
        this.size = size;
    }

    @Override
    public FontFamily getFamily() {
        return family;
    }

    @Override
    public ResourceLocation getLocation() {
        return location;
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
    public Font derive(final FontStyle style) {
        return family.getFont(style, size);
    }

    @Override
    public Font derive(final float size) {
        return family.getFont(style, size);
    }
}
