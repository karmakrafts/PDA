/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.sync;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 18/04/2024
 */
@FunctionalInterface
public interface SyncDecodeFunction<T> {
    @Nullable
    T decode(final String name, final ObjectNode node);
}
