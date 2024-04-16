/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.view;

import io.karma.pda.api.common.API;
import io.karma.pda.api.common.app.App;
import io.karma.pda.api.common.app.AppContext;
import io.karma.pda.api.common.app.component.Container;
import io.karma.pda.api.common.app.component.DefaultComponents;
import io.karma.pda.api.common.app.component.DefaultContainer;

import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public class DefaultContainerView implements AppView {
    private final DefaultContainer container = DefaultComponents.CONTAINER.create();
    private final String name;
    private final Consumer<DefaultContainer> callback;

    public DefaultContainerView(final String name, final Consumer<DefaultContainer> callback) {
        this.name = name;
        this.callback = callback;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void build(final App app, final AppContext context) {
        API.getLogger().debug("Building view '{}'", name);
        callback.accept(container);
        app.getView().getContainer().addChild(container);
    }
}
