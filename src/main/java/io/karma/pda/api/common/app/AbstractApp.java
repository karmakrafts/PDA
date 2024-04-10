/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app;

import io.karma.pda.api.common.app.component.Container;
import io.karma.pda.api.common.app.component.DefaultContainer;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public abstract class AbstractApp implements App {
    protected final AppType<?> type;
    protected final DefaultContainer container = new DefaultContainer(UUID.randomUUID());

    public AbstractApp(final AppType<?> type) {
        this.type = type;
    }

    @Override
    public AppType<?> getType() {
        return type;
    }

    @Override
    public Container getContainer() {
        return container;
    }
}
