/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.karma.sliced.view.View;

/**
 * @author Alexander Hinze
 * @since 18/02/2024
 */
public interface Container extends Component {
    void addChild(final Component child);

    void removeChild(final Component child);

    View<Component> getChildren();

    @Override
    default void serialize(final ObjectNode node) {
        for (final var child : getChildren()) {
            child.serialize(node);
        }
    }

    @Override
    default void deserialize(final ObjectNode node) {
        for (final var child : getChildren()) {
            child.deserialize(node);
        }
    }
}
