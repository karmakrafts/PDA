/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import io.karma.pda.api.common.API;
import io.karma.pda.api.common.app.event.ClickEvent;
import io.karma.pda.api.common.app.event.MouseMoveEvent;
import io.karma.pda.api.common.flex.DefaultFlexNode;
import io.karma.pda.api.common.flex.FlexNode;
import io.karma.pda.api.common.sync.Sync;
import io.karma.pda.api.common.sync.Synced;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public abstract class AbstractComponent implements Component {
    protected final ComponentType<?> type;
    protected final UUID id;
    protected final FlexNode flexNode = DefaultFlexNode.defaults();
    protected Component parent;
    protected Consumer<ClickEvent> clickEventConsumer = event -> {
    };
    protected Consumer<MouseMoveEvent> mouseOverEventConsumer = event -> {
    };
    protected Consumer<MouseMoveEvent> mouseEnterEventConsumer = event -> {
    };
    protected Consumer<MouseMoveEvent> mouseExitEventConsumer = event -> {
    };

    // Internal synchronized property for tracking visibility state
    @Sync
    protected final Synced<Boolean> isVisible = Synced.of(true);

    protected AbstractComponent(final ComponentType<?> type, final UUID id) {
        this.type = type;
        this.id = id;
        API.getLogger().debug("Creating component {} of type {}", id, type.getName());
    }

    @Override
    public void setVisible(final boolean isVisible) {
        this.isVisible.set(isVisible);
    }

    @Override
    public boolean isVisible() {
        return isVisible.get();
    }

    @Override
    public void dispose() {
        API.getLogger().debug("Disposing component {} of type {}", id, type.getName());
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public @Nullable Component getParent() {
        return parent;
    }

    @Override
    public void setParent(final @Nullable Component parent) {
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
        return id.equals(component.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
