/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.karma.pda.api.common.app.component.Component;
import io.karma.pda.api.common.util.JSONUtils;

import java.io.IOException;

/**
 * @author Alexander Hinze
 * @since 17/02/2024
 */
public final class ComponentSerializer extends StdSerializer<Component> {
    public ComponentSerializer() {
        super(Component.class);
    }

    @Override
    public void serialize(final Component component, final JsonGenerator generator,
                          final SerializerProvider provider) throws IOException {
        final var rootNode = JSONUtils.MAPPER.createObjectNode();
        rootNode.put("id", component.getType().getName().toString());
        component.serialize(rootNode);
        JSONUtils.MAPPER.writeTree(generator, rootNode);
    }
}
