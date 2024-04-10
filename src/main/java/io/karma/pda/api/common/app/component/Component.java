/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import io.karma.pda.api.common.dispose.Disposable;
import io.karma.pda.api.common.flex.FlexSpec;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public interface Component extends Disposable {
    ComponentType<?> getType();

    UUID getUUID();

    @Nullable
    Component getParent();

    void setParent(final @Nullable Component parent);

    FlexSpec getLayoutSpec();

    @Override
    default void dispose() {
    }
}
