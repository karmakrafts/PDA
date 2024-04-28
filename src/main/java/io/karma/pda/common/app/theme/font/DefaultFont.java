/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.app.theme.font;

import io.karma.pda.api.common.app.theme.font.Font;
import io.karma.pda.api.common.app.theme.font.FontFamily;
import io.karma.pda.api.common.app.theme.font.FontStyle;
import net.minecraft.resources.ResourceLocation;

/**
 * @author Alexander Hinze
 * @since 28/04/2024
 */
public final class DefaultFont implements Font {
    private final FontFamily family;
    private final ResourceLocation name;
    private final FontStyle style;
    private final float size;

    DefaultFont(final FontFamily family, final ResourceLocation name, final FontStyle style, final float size) {
        this.family = family;
        this.name = name;
        this.style = style;
        this.size = size;
    }

    @Override
    public FontFamily getFamily() {
        return family;
    }

    @Override
    public ResourceLocation getName() {
        return name;
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
