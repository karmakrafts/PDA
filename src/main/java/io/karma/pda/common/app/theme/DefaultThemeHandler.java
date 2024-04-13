/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.app.theme;

import io.karma.pda.api.common.app.theme.Theme;
import io.karma.pda.api.common.app.theme.ThemeHandler;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public final class DefaultThemeHandler implements ThemeHandler {
    public static final DefaultThemeHandler INSTANCE = new DefaultThemeHandler();
    private final HashMap<ResourceLocation, Theme> themes = new HashMap<>();

    // @formatter:off
    private DefaultThemeHandler() {}
    // @formatter:on

    @Override
    public void addTheme(final Theme theme) {
        final var name = theme.getName();
        if (themes.containsKey(name)) {
            throw new IllegalArgumentException(String.format("Theme %s already exists", name));
        }
        themes.put(name, theme);
    }

    @Override
    public void removeTheme(final Theme theme) {
        final var name = theme.getName();
        if (themes.remove(name) == null) {
            throw new IllegalArgumentException(String.format("Theme %s does not exist", name));
        }
    }

    @Override
    public @Nullable Theme getTheme(final ResourceLocation name) {
        return themes.get(name);
    }
}
