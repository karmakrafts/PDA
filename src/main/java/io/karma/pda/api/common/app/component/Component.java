/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import io.karma.pda.api.common.flex.FlexNode;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public interface Component {
    ComponentType<?> getType();

    UUID getUUID();

    @Nullable
    Component getParent();

    void setParent(final @Nullable Component parent);

    FlexNode getLayoutSpec();
}
