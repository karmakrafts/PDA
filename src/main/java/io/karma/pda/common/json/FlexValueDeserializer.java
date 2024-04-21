/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import io.karma.pda.api.common.flex.FlexValue;
import io.karma.pda.api.common.flex.FlexValueType;

import java.io.IOException;

/**
 * @author Alexander Hinze
 * @since 21/04/2024
 */
public final class FlexValueDeserializer extends StdDeserializer<FlexValue> {
    public FlexValueDeserializer() {
        super(FlexValue.class);
    }

    @Override
    public FlexValue deserialize(final JsonParser parser,
                                 final DeserializationContext deserializationContext) throws IOException {
        final var node = parser.getCodec().readTree(parser);
        final var type = FlexValueType.values()[((IntNode) node.get("type")).intValue()];
        final var value = ((FloatNode) node.get("value")).floatValue();
        return new FlexValue() {
            @Override
            public FlexValueType getType() {
                return type;
            }

            @Override
            public float get() {
                return value;
            }
        };
    }
}
