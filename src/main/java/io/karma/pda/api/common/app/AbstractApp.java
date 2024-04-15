/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app;

import io.karma.pda.api.common.app.theme.Theme;
import io.karma.pda.api.common.app.view.AppView;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public abstract class AbstractApp implements App {
    protected final AppType<?> type;
    protected final HashMap<String, AppView> views = new HashMap<>();
    protected final Theme theme;
    protected String currentView = DEFAULT_VIEW;

    public AbstractApp(final AppType<?> type, final Theme theme) {
        this.type = type;
        this.theme = theme;
    }

    @Override
    public void addView(final String name, final AppView view) {
        if (views.containsKey(name)) {
            throw new IllegalArgumentException(String.format("View %s already exists", name));
        }
        views.put(name, view);
    }

    @Override
    public @Nullable AppView removeView(final String name) {
        return views.remove(name);
    }

    @Override
    public void setView(final String name) {
        currentView = name;
    }

    @Override
    public String getViewName() {
        return currentView;
    }

    @Override
    public AppView getView() {
        return Objects.requireNonNull(views.get(currentView));
    }

    @Override
    public void dispose(final AppContext context) {
        for (final var view : views.values()) {
            view.dispose();
        }
    }

    @Override
    public AppType<?> getType() {
        return type;
    }

    @Override
    public Theme getTheme() {
        return theme;
    }
}
