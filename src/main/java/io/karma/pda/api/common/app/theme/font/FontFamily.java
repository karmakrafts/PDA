/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.theme.font;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author Alexander Hinze
 * @since 28/04/2024
 */
public interface FontFamily {
    ResourceLocation getName();

    String getDisplayName();

    Set<FontStyle> getStyles();

    Font getFont(final FontStyle style, final float size);

    @SuppressWarnings("all")
    @NotNull static FontFamily nullType() {
        return null;
    }
}
