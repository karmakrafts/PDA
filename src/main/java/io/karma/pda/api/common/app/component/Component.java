/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import io.karma.pda.api.common.app.event.ClickEvent;
import io.karma.pda.api.common.app.event.MouseMoveEvent;
import io.karma.pda.api.common.flex.FlexNode;
import io.karma.pda.api.common.util.Identifiable;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public interface Component extends Identifiable {
    default void dispose() {
    }

    ComponentType<?> getType();

    @Nullable
    Component getParent();

    void setParent(final @Nullable Component parent);

    FlexNode getFlexNode();

    void setVisible(final boolean isVisible);

    boolean isVisible();

    void onClicked(final Consumer<ClickEvent> callback);

    void onMouseOver(final Consumer<MouseMoveEvent> callback);

    void onMouseEnter(final Consumer<MouseMoveEvent> callback);

    void onMouseExit(final Consumer<MouseMoveEvent> callback);
}
