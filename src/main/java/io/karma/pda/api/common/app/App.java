/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app;

import io.karma.pda.api.common.app.component.Container;
import io.karma.pda.api.common.app.theme.Theme;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public interface App {
    void init(final AppContext context);

    default void reload(final AppContext context) {
    }

    AppType<?> getType();

    Container getContainer();

    Theme getTheme();
}
