/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.app.theme.font;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author Alexander Hinze
 * @since 28/04/2024
 */
public interface FontFamily {
    @SuppressWarnings("all")
    @NotNull
    static FontFamily nullType() {
        return null;
    }

    ResourceLocation getName();

    String getDisplayName();

    Set<FontStyle> getStyles();

    int getGlyphSpriteSize();

    int getGlyphSpriteBorder();

    float getDistanceFieldRange();

    FontVariant getFont(final FontStyle style, final float size);

    FontVariant getFont(final FontStyle style, final float size, final Object2FloatMap<String> variationAxes);

    default FontVariant getDefaultFont() {
        return getFont(FontStyle.REGULAR, FontVariant.DEFAULT_SIZE);
    }
}
