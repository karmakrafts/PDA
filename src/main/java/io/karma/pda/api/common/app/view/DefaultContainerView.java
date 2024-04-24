/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.view;

import io.karma.pda.api.common.app.component.Container;
import io.karma.pda.api.common.app.component.DefaultComponents;
import io.karma.pda.api.common.app.component.DefaultContainer;
import io.karma.pda.api.common.flex.FlexPositionType;
import io.karma.pda.api.common.flex.FlexValue;

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
