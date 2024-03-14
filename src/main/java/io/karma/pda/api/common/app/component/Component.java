/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public interface Component {
    ComponentType<?> getType();

    @Nullable Component getParent();

    void setParent(final @Nullable Component parent);

    default void serialize(final ObjectNode node) {
    }

    default void deserialize(final ObjectNode node) {
    }
}
