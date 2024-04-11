/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import io.karma.pda.api.common.flex.FlexNode;
import io.karma.pda.api.common.util.Identifiable;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public interface Component extends Identifiable {
    ComponentType<?> getType();

    @Nullable
    Component getParent();

    void setParent(final @Nullable Component parent);

    FlexNode getLayoutSpec();
}
