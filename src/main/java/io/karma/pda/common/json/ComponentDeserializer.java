/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;
import io.karma.pda.api.common.API;
import io.karma.pda.api.common.app.component.Component;
import io.karma.pda.api.common.app.component.Container;
import io.karma.pda.api.common.flex.FlexNode;
import io.karma.pda.api.common.util.JSONUtils;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 21/04/2024
 */
public final class ComponentDeserializer extends StdDeserializer<Component> {
    public ComponentDeserializer() {
        super(Component.class);
    }

    @Override
    public Component deserialize(final JsonParser parser,
                                 final DeserializationContext deserializationContext) throws IOException {
        final var node = parser.getCodec().readTree(parser);

        final var typeName = ResourceLocation.tryParse(((TextNode) node.get("type")).asText());
        if (typeName == null) {
            return null;
        }

        final var type = API.getComponentTypeRegistry().getValue(typeName);
        if (type == null) {
            return null;
        }

        final var id = UUID.fromString(((TextNode) node.get("id")).asText());
        final var constraints = JSONUtils.MAPPER.treeToValue(node.get("constraints"), FlexNode.class);
        final var component = type.create(id, props -> props.from(constraints));

        if (component instanceof Container container) {
            final var childNodes = (ArrayNode) node.get("children");
            for (final var childNode : childNodes) {
                container.addChild(JSONUtils.MAPPER.treeToValue(childNode, Component.class));
            }
        }

        return component;
    }
}
