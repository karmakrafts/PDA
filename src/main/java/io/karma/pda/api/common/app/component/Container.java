/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import io.karma.pda.api.common.app.event.ClickEvent;
import io.karma.pda.api.common.app.event.MouseMoveEvent;
import io.karma.pda.api.common.util.Proxy;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 18/02/2024
 */
public interface Container extends Component {
    void addChild(final Component child);

    void removeChild(final Component child);

    @Nullable
    Component findChild(final UUID id);

    @Nullable
    Component findChild(final String localName);

    @Nullable
    Component findChildRecursively(final UUID id);

    @Nullable
    Component findChildRecursively(final String localName);

    default Proxy<? extends Component> findChildProxy(final String localName) {
        return () -> findChild(localName);
    }

    default Proxy<? extends Component> findChildProxy(final UUID id) {
        return () -> findChild(id);
    }

    default Proxy<? extends Component> findChildRecursivelyProxy(final String localName) {
        return () -> findChildRecursively(localName);
    }

    default Proxy<? extends Component> findChildRecursivelyProxy(final UUID id) {
        return () -> findChildRecursively(id);
    }

    Collection<Component> getChildren();

    @Override
    default void onClicked(final Consumer<ClickEvent> callback) {
        getChildren().forEach(component -> component.onClicked(callback));
    }

    @Override
    default void onMouseOver(final Consumer<MouseMoveEvent> callback) {
        getChildren().forEach(component -> component.onMouseOver(callback));
    }

    @Override
    default void onMouseEnter(final Consumer<MouseMoveEvent> callback) {
        getChildren().forEach(component -> component.onMouseEnter(callback));
    }

    @Override
    default void onMouseExit(final Consumer<MouseMoveEvent> callback) {
        getChildren().forEach(component -> component.onMouseExit(callback));
    }
}
