/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.theme;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public interface ThemeHandler {
    void addTheme(final Theme theme);

    void removeTheme(final Theme theme);

    @Nullable
    Theme getTheme(final ResourceLocation name);
}
