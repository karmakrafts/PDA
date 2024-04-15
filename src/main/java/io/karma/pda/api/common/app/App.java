/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app;

import io.karma.pda.api.common.app.component.Container;
import io.karma.pda.api.common.app.component.DefaultContainer;
import io.karma.pda.api.common.app.theme.Theme;
import io.karma.pda.api.common.app.view.AppView;
import io.karma.pda.api.common.app.view.DefaultContainerView;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public interface App {
    String DEFAULT_VIEW = "default";

    void init(final AppContext context);

    default void dispose(final AppContext context) {
    }

    AppType<?> getType();

    Theme getTheme();

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

    String getViewName();

    AppView getView();

    default Container getContainer() {
        return getView().getContainer();
    }
}
