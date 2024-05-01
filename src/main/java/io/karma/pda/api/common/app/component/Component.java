/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import io.karma.pda.api.common.app.event.ClickEvent;
import io.karma.pda.api.common.app.event.MouseMoveEvent;
import io.karma.pda.api.common.flex.FlexNode;
import io.karma.pda.api.common.util.Identifiable;
import io.karma.pda.api.common.util.Proxy;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public interface Component extends Identifiable {
    ComponentType<?> getType();

    void setId(final UUID id);

    void setLocalName(final @Nullable String localName);

    @Nullable
    String getLocalName();

    @Nullable
    Component getParent();

    default Proxy<? extends Component> getParentProxy() {
        return this::getParent;
    }

    void setParent(final @Nullable Component parent);

    FlexNode getFlexNode();

    void setVisible(final boolean isVisible);

    boolean isVisible();

    void onClicked(final Consumer<ClickEvent> callback);

    void onMouseOver(final Consumer<MouseMoveEvent> callback);

    void onMouseEnter(final Consumer<MouseMoveEvent> callback);

    void onMouseExit(final Consumer<MouseMoveEvent> callback);
}
