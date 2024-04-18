/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.sync;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.karma.pda.api.common.API;
import io.karma.pda.api.common.util.JSONUtils;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 18/04/2024
 */
public final class DefaultSyncCodec implements SyncCodec<Object> {
    @Override
    public @Nullable Object decode(final String name, final ObjectNode node) {
        try {
            final var childNode = node.get(name);
            final var typeName = childNode.get("type").asText();
            final var type = Class.forName(typeName);
            final var valueNode = childNode.get("value");
            if (valueNode == null || valueNode.isNull()) {
                return null;
            }
            return JSONUtils.MAPPER.treeToValue(valueNode, type);
        }
        catch (Throwable error) {
            API.getLogger().error("Could not decode synchronized value: {}", error.getMessage());
            return null;
        }
    }

    @Override
    public void encode(final String name, final Class<Object> type, final @Nullable Object value,
                       final ObjectNode node) {
        final var childNode = JSONUtils.MAPPER.createObjectNode();
        childNode.put("type", type.getName());
        if (value != null) {
            childNode.set("value", JSONUtils.MAPPER.valueToTree(value));
        }
        else {
            childNode.set("value", null);
        }
        node.set(name, childNode);
    }
}
