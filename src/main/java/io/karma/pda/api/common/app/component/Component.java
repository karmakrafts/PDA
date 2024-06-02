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

    @Nullable
    String getLocalName();

    void setLocalName(final @Nullable String localName);

    @Nullable
    Container getParent();

    void setParent(final @Nullable Container parent);

    default Proxy<? extends Container> getParentProxy() {
        return this::getParent;
    }

    FlexNode getFlexNode();

    boolean isVisible();

    void setVisible(final boolean isVisible);

    void onClicked(final Consumer<ClickEvent> callback);

    void onMouseOver(final Consumer<MouseMoveEvent> callback);

    void onMouseEnter(final Consumer<MouseMoveEvent> callback);

    void onMouseExit(final Consumer<MouseMoveEvent> callback);
}
