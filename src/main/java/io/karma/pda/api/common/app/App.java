/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app;

import io.karma.pda.api.common.app.component.Container;
import io.karma.pda.api.common.app.theme.Theme;
import io.karma.pda.api.common.app.view.AppView;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public interface App {
    void init(final AppContext context);

    default void dispose(final AppContext context) {
    }

    AppType<?> getType();

    Theme getTheme();

    void setView(final String name);

    String getViewName();

    AppView getView();

    default Container getContainer() {
        return getView().getContainer();
    }
}
