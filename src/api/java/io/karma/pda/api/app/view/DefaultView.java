/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.app.view;

import io.karma.pda.api.app.component.Container;

/**
 * @author Alexander Hinze
 * @since 16/04/2024
 */
public class DefaultView implements AppView {
    private final String name;
    private final Container container;

    public DefaultView(final String name, final Container container) {
        this.name = name;
        this.container = container;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Container getContainer() {
        return container;
    }
}
