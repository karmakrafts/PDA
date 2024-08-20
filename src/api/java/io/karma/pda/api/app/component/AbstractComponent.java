/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.app.component;

import io.karma.pda.api.app.event.ClickEvent;
import io.karma.pda.api.app.event.MouseMoveEvent;
import io.karma.pda.api.flex.DefaultFlexNode;
import io.karma.pda.api.flex.FlexNode;
import io.karma.pda.api.state.MutableState;
import io.karma.pda.api.state.Synchronize;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public abstract class AbstractComponent implements Component {
    protected final ComponentType<?> type;
    protected final AtomicReference<UUID> id = new AtomicReference<>();
    protected final FlexNode flexNode = DefaultFlexNode.defaults();
    // Internal synchronized property for tracking visibility state
    @Synchronize
    protected final MutableState<Boolean> isVisible = MutableState.of(true);
    protected Container parent;
    protected String localName;
    protected Consumer<ClickEvent> clickEventConsumer = event -> {
    };
    protected Consumer<MouseMoveEvent> mouseOverEventConsumer = event -> {
    };
    protected Consumer<MouseMoveEvent> mouseEnterEventConsumer = event -> {
    };
    protected Consumer<MouseMoveEvent> mouseExitEventConsumer = event -> {
    };

    protected AbstractComponent(final ComponentType<?> type, final UUID id) {
        this.type = type;
        this.id.set(id);
    }

    @Override
    public @Nullable String getLocalName() {
        return localName;
    }

    @Override
    public void setLocalName(final @Nullable String localName) {
        this.localName = localName;
    }

    @Override
    public boolean isVisible() {
        return isVisible.get();
    }

    @Override
    public void setVisible(final boolean isVisible) {
        this.isVisible.set(isVisible);
    }

    @Override
    public UUID getId() {
        return id.get();
    }

    @Override
    public void setId(final UUID id) {
        this.id.set(id);
    }

    @Override
    public @Nullable Container getParent() {
        return parent;
    }

    @Override
    public void setParent(final @Nullable Container parent) {
        this.parent = parent;
    }

    @Override
    public ComponentType<?> getType() {
        return type;
    }

    @Override
    public FlexNode getFlexNode() {
        return flexNode;
    }

    @Override
    public void onClicked(final Consumer<ClickEvent> callback) {
        clickEventConsumer = clickEventConsumer.andThen(callback);
    }

    @Override
    public void onMouseOver(final Consumer<MouseMoveEvent> callback) {
        mouseOverEventConsumer = mouseOverEventConsumer.andThen(callback);
    }

    @Override
    public void onMouseEnter(final Consumer<MouseMoveEvent> callback) {
        mouseEnterEventConsumer = mouseEnterEventConsumer.andThen(callback);
    }

    @Override
    public void onMouseExit(final Consumer<MouseMoveEvent> callback) {
        mouseExitEventConsumer = mouseExitEventConsumer.andThen(callback);
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", type.getName(), id);
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Component component)) {
            return false;
        }
        return id.get().equals(component.getId());
    }

    @Override
    public int hashCode() {
        return id.get().hashCode();
    }
}
