/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.karma.pda.api.common.API;
import io.karma.pda.api.common.app.component.Component;
import io.karma.pda.api.common.app.component.ComponentType;
import io.karma.pda.api.common.app.component.Container;
import io.karma.pda.api.common.util.JSONUtils;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 16/04/2024
 */
public final class ComponentCodec {
    // @formatter:off
    private ComponentCodec() {}
    // @formatter:on

    public static void encode(final ObjectNode node, final @Nullable Component component) {
        if (component == null) {
            return;
        }
        node.put("type", component.getType().getName().toString());
        node.put("id", component.getId().toString());
        if (component instanceof Container container) {
            final var children = container.getChildren();
            final var childrenNode = JSONUtils.MAPPER.createArrayNode();
            for (final var child : children) {
                final var childNode = JSONUtils.MAPPER.createObjectNode();
                encode(childNode, child);
                childrenNode.add(childNode);
            }
            node.set("children", childrenNode);
        }
        API.getLogger().debug("Encoded component:\n{}\n", node.toPrettyString());
    }

    public static byte[] encode(final @Nullable Component component) {
        if (component == null) {
            return new byte[0];
        }
        final var node = JSONUtils.MAPPER.createObjectNode();
        encode(node, component);
        return JSONUtils.compress(node);
    }

    public static @Nullable Component decode(final ObjectNode node) {
        try {
            final var typeName = ResourceLocation.tryParse(Objects.requireNonNull(node.get("type")).asText());
            final var type = API.getComponentTypeRegistry().getValue(typeName);
            if (type == null) {
                return null; // TODO: warn?
            }
            final var id = UUID.fromString(Objects.requireNonNull(node.get("id")).asText());
            final var result = type.create(id, props -> {
            });
            if (result instanceof Container container) {
                final var childrenNode = Objects.requireNonNull(node.get("children"));
                for (final var childNode : childrenNode) {
                    if (!(childNode instanceof ObjectNode objectChildNode)) {
                        continue; // TODO: warn?
                    }
                    container.addChild(decode(objectChildNode));
                }
            }
            return result;
        }
        catch (Throwable error) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <C extends Component> @Nullable C decode(final ObjectNode node, final ComponentType<C> type) {
        final var component = decode(node);
        if (component == null || component.getType() != type) {
            return null;
        }
        return (C) component;
    }

    public static @Nullable Component decode(final byte[] data) {
        return decode(JSONUtils.decompress(data, ObjectNode.class));
    }

    public static <C extends Component> @Nullable C decode(final byte[] data, final ComponentType<C> type) {
        return decode(JSONUtils.decompress(data, ObjectNode.class), type);
    }
}
