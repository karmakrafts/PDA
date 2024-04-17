/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app;

import io.karma.pda.api.common.app.view.AppView;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public abstract class AbstractApp implements App {
    protected final AppType<?> type;
    protected final AppState state = new AppState();
    protected final HashMap<String, AppView> views = new HashMap<>();
    protected String currentView = DEFAULT_VIEW;

    public AbstractApp(final AppType<?> type) {
        this.type = type;
    }

    @Override
    public AppState getState() {
        return state;
    }

    @Override
    public void init() {
        for (final var view : views.values()) {
            view.build(this);
        }
    }

    @Override
    public void addView(final String name, final AppView view) {
        if (views.containsKey(name)) {
            throw new IllegalArgumentException(String.format("View '%s' already exists", name));
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
    public void clearViews() {
        views.clear();
    }

    @Override
    public AppView getView() {
        return Objects.requireNonNull(views.get(currentView));
    }

    @Override
    public Collection<AppView> getViews() {
        return Collections.unmodifiableCollection(views.values());
    }

    @Override
    public void dispose() {
        for (final var view : views.values()) {
            view.dispose();
        }
    }

    @Override
    public AppType<?> getType() {
        return type;
    }
}
