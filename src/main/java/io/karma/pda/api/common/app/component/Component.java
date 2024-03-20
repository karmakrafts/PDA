/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.karma.pda.api.common.dispose.Disposable;
import io.karma.pda.api.common.flex.FlexNode;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public interface Component extends Disposable {
    ComponentType<?> getType();

    UUID getUUID();

    @Nullable Component getParent();

    void setParent(final @Nullable Component parent);

    FlexNode getFlexboxNode();

    default void serialize(final ObjectNode node) {
    }

    default void deserialize(final ObjectNode node) {
    }

    @Override
    default void dispose() {}
}
