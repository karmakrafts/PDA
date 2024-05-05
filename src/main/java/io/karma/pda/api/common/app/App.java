/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app;

import io.karma.pda.api.common.app.component.DefaultContainer;
import io.karma.pda.api.common.app.view.AppView;
import io.karma.pda.api.common.app.view.DefaultContainerView;
import io.karma.pda.api.common.session.Session;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public interface App {
    String DEFAULT_VIEW = "default";

    boolean isInitialized();

    /**
     * Called to compose the actual layout of the app.
     * This function may be called multiple times as needed,
     * so initialization logic should exclusively go into {@link #init(Session)}.
     */
    void compose();

    /**
     * Called to initialize the app after it has been composed.
     *
     * @param session The session for which this app was opened for.
     */
    default void init(final Session session) {
    }

    /**
     * Called when the app is closed.
     */
    default void dispose() {
    }

    AppType<?> getType();

    void addView(final String name, final AppView view);

    default void addView(final String name, final Consumer<DefaultContainer> callback) {
        addView(name, new DefaultContainerView(name, callback));
    }

    default void addDefaultView(final AppView view) {
        addView(DEFAULT_VIEW, view);
    }

    default void addDefaultView(final Consumer<DefaultContainer> callback) {
        addView(DEFAULT_VIEW, callback);
    }

    @Nullable
    AppView removeView(final String name);

    void setView(final String name);

    void pushView(final String name);

    @Nullable
    AppView popView();

    String getViewName();

    AppView getView();

    Collection<AppView> getViews();

    void clearViews();

    AppState getState();
}
