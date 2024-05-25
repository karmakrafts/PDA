/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.theme.font;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.resources.ResourceLocation;

/**
 * @author Alexander Hinze
 * @since 28/04/2024
 */
public interface Font {
    FontFamily getFamily();

    FontCharSet getSupportedChars();

    ResourceLocation getLocation();

    FontVariant getDefaultVariant();

    Object2FloatMap<String> getVariationAxes();

    float getVariationAxis(final String name);

    default FontVariant asVariant() {
        return this instanceof FontVariant variant ? variant : getDefaultVariant();
    }
}
