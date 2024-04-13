/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.view;

import io.karma.pda.api.common.app.App;
import io.karma.pda.api.common.app.AppContext;
import io.karma.pda.api.common.app.component.Container;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public interface AppView {
    String getName();

    void build(final App app, final AppContext context);

    Container getContainer();

    default void dispose() {
    }
}
