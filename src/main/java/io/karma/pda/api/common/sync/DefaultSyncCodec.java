/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.sync;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.karma.pda.api.common.util.JSONUtils;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 18/04/2024
 */
public final class DefaultSyncCodec implements SyncCodec<Object> {
    @Override
    public @Nullable Object decode(final String name, final ObjectNode node) {
        final var childNode = node.get(name);
        if (!(childNode instanceof ObjectNode objectNode)) {
            return null;
        }
        final var pair = JSONUtils.decodeObject(objectNode);
        if (pair == null) {
            return null;
        }
        return pair.getRight();
    }

    @Override
    public void encode(final String name, final Class<Object> type, final @Nullable Object value,
                       final ObjectNode node) {
        node.set(name, JSONUtils.encodeObject(type, value));
    }

    @Override
    public Class<Object> getType() {
        return Object.class;
    }
}
