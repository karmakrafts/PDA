/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app;

import io.karma.pda.api.common.app.component.DefaultContainer;
import io.karma.pda.api.common.app.theme.Theme;
import io.karma.pda.api.common.app.component.Container;
import io.karma.pda.api.common.app.component.DefaultComponents;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public abstract class AbstractApp implements App {
    protected final AppType<?> type;
    protected final DefaultContainer container = DefaultComponents.CONTAINER.create();
    protected final Theme theme;

    public AbstractApp(final AppType<?> type, final Theme theme) {
        this.type = type;
        this.theme = theme;
    }

    @Override
    public AppType<?> getType() {
        return type;
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public Theme getTheme() {
        return theme;
    }
}
