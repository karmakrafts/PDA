/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.karma.pda.api.common.API;
import io.karma.pda.api.common.flex.FlexBorder;
import io.karma.pda.api.common.flex.FlexValue;

import java.io.IOException;

/**
 * @author Alexander Hinze
 * @since 21/04/2024
 */
public final class FlexBorderDeserializer extends StdDeserializer<FlexBorder> {
    public FlexBorderDeserializer() {
        super(FlexBorder.class);
    }

    @Override
    public FlexBorder deserialize(final JsonParser parser,
                                  final DeserializationContext deserializationContext) throws IOException {
        final var node = parser.getCodec().readTree(parser);
        final var mapper = API.getObjectMapper();
        final var left = mapper.treeToValue(node.get(0), FlexValue.class);
        final var top = mapper.treeToValue(node.get(1), FlexValue.class);
        final var right = mapper.treeToValue(node.get(2), FlexValue.class);
        final var bottom = mapper.treeToValue(node.get(3), FlexValue.class);
        return FlexBorder.of(left, right, top, bottom);
    }
}
