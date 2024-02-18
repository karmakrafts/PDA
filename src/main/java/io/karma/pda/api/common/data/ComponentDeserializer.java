/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.data;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.karma.pda.api.common.API;
import io.karma.pda.api.common.app.component.Component;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

/**
 * @author Alexander Hinze
 * @since 17/02/2024
 */
public final class ComponentDeserializer extends StdDeserializer<Component> {
    public ComponentDeserializer() {
        super(Component.class);
    }

    @Override
    public Component deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
        final var rootNode = (ObjectNode) parser.getCodec().readTree(parser);
        final var idNode = rootNode.get("id");
        if (idNode == null) {
            throw new IOException("Missing component ID");
        }
        final var id = ResourceLocation.tryParse(idNode.asText());
        if (id == null) {
            throw new IOException("Could not parse component ID");
        }
        final var type = API.getComponentTypeRegistry().getValue(id);
        if (type == null) {
            throw new IOException("Could not find component type");
        }
        final var component = type.create();
        component.deserialize(rootNode);
        return component;
    }
}
