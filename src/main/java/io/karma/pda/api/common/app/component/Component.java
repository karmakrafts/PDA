/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public interface Component {
    ComponentType<?> getType();

    @Nullable Component getParent();

    void setParent(final @Nullable Component parent);

    default void addChild(final Component child) {
        throw new UnsupportedOperationException();
    }

    default void removeChild(final Component child) {
        throw new UnsupportedOperationException();
    }

    default List<Component> getChildren() {
        return Collections.emptyList();
    }

    default void serialize(final ObjectNode node) {
        for (final var child : getChildren()) {
            child.serialize(node);
        }
    }

    default void deserialize(final ObjectNode node) {
        for (final var child : getChildren()) {
            child.deserialize(node);
        }
    }
}
