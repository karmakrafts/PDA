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

    FontCharSet getSupportedChars();

    ResourceLocation getLocation();

    FontVariant getDefaultVariant();
}
