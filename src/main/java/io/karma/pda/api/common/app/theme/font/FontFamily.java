/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.theme.font;

import net.minecraft.resources.ResourceLocation;

import java.util.EnumSet;

/**
 * @author Alexander Hinze
 * @since 28/04/2024
 */
public interface FontFamily {
    ResourceLocation getName();

    EnumSet<FontStyle> getStyles();

    Font getFont(final FontStyle style);
}
