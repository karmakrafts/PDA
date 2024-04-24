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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public abstract class AbstractApp implements App {
    protected final AppType<?> type;
    protected final AppState state = new AppState();
    protected final HashMap<String, AppView> views = new HashMap<>();
    protected final AtomicReference<String> currentView = new AtomicReference<>(DEFAULT_VIEW);
    protected final AtomicBoolean isInitialized = new AtomicBoolean(false);

    public AbstractApp(final AppType<?> type) {
        this.type = type;
    }

    @Override
    public void init() {
        if (!isInitialized.compareAndSet(false, true)) {
            throw new IllegalStateException("Already initialized");
        }
    }

    @Override
    public boolean isInitialized() {
        return isInitialized.get();
    }

    @Override
    public void setInitialized(final boolean isInitialized) {
        this.isInitialized.set(isInitialized);
    }

    @Override
    public AppState getState() {
        return state;
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
        currentView.set(name);
    }

    @Override
    public String getViewName() {
        return currentView.get();
    }

    @Override
    public void clearViews() {
        views.clear();
    }

    @Override
    public AppView getView() {
        return Objects.requireNonNull(views.get(currentView.get()));
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
