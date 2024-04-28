/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.theme.font;

import net.minecraft.resources.ResourceLocation;

/**
 * @author Alexander Hinze
 * @since 28/04/2024
 */
public interface Font {
    FontFamily getFamily();

    ResourceLocation getName();

    FontStyle getStyle();

    float getSize();

    Font derive(final FontStyle style);

    Font derive(final float size);

    default Font derive(final FontStyle style, final float size) {
        return derive(style).derive(size);
    }
}
