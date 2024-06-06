/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.app;

import io.karma.pda.api.app.view.AppView;
import io.karma.pda.api.session.Session;
import io.karma.pda.api.state.MutableState;
import io.karma.pda.api.state.Synchronize;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public abstract class AbstractApp implements App {
    protected final AppType<?> type;
    protected final ConcurrentHashMap<String, AppView> views = new ConcurrentHashMap<>();
    protected final AtomicBoolean isInitialized = new AtomicBoolean(false);

    @Synchronize
    protected final MutableState<Stack<String>> viewStack = MutableState.of(new Stack<>());
    @Synchronize
    protected final MutableState<AppState> state = MutableState.of(new AppState());

    public AbstractApp(final AppType<?> type) {
        this.type = type;
        viewStack.get().push(DEFAULT_VIEW);
    }

    @Override
    public void init(final Session session) {
        if (!isInitialized.compareAndSet(false, true)) {
            throw new IllegalStateException("Already initialized");
        }
    }

    @Override
    public boolean isInitialized() {
        return isInitialized.get();
    }

    @Override
    public AppState getState() {
        return state.get();
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
    public void pushView(final String name) {
        viewStack.get().push(name);
    }

    @Override
    public @Nullable AppView popView() {
        return views.get(viewStack.get().pop());
    }

    @Override
    public String getViewName() {
        return viewStack.get().peek();
    }

    @Override
    public void clearViews() {
        views.clear();
    }

    @Override
    public AppView getView() {
        return Objects.requireNonNull(views.get(getViewName()));
    }

    @Override
    public void setView(final String name) {
        final var stack = viewStack.get();
        stack.set(stack.size() - 1, name);
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
