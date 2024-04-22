/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.karma.pda.api.common.app.component.Component;
import io.karma.pda.api.common.app.component.Container;

import java.io.IOException;

/**
 * @author Alexander Hinze
 * @since 21/04/2024
 */
public final class ComponentSerializer extends StdSerializer<Component> {
    public ComponentSerializer() {
        super(Component.class);
    }

    @Override
    public void serialize(final Component component, final JsonGenerator generator,
                          final SerializerProvider serializerProvider) throws IOException {
        generator.writeStartObject();
        generator.writeStringField("type", component.getType().getName().toString());
        generator.writeStringField("id", component.getId().toString());
        generator.writeObjectField("constraints", component.getFlexNode());

        if (component instanceof Container container) {
            generator.writeArrayFieldStart("children");
            for (final var child : container.getChildren()) {
                serialize(child, generator, serializerProvider);
            }
            generator.writeEndArray();
        }

        generator.writeEndObject();
    }
}
