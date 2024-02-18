/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.data;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.karma.pda.api.common.app.component.Component;

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
        final var rootNode = (ObjectNode) context.readTree(parser);
        return null;
    }
}
