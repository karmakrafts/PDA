/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.foundation.view;

import io.karma.pda.api.app.component.Container;
import io.karma.pda.api.app.view.AppView;
import io.karma.pda.api.flex.FlexPositionType;
import io.karma.pda.api.flex.FlexValue;
import io.karma.pda.foundation.component.DefaultComponents;
import io.karma.pda.foundation.component.DefaultContainer;

import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public class DefaultContainerView implements AppView {
    // @formatter:off
    private final DefaultContainer container = DefaultComponents.CONTAINER.create(props -> props
        .width(FlexValue.percent(100F))
        .height(FlexValue.percent(100F))
        .positionType(FlexPositionType.ABSOLUTE));
    // @formatter:on
    private final String name;

    public DefaultContainerView(final String name, final Consumer<DefaultContainer> callback) {
        this.name = name;
        callback.accept(container);
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
