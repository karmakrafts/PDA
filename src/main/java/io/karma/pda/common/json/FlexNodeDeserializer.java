/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;
import io.karma.pda.api.common.API;
import io.karma.pda.api.common.flex.*;

import java.io.IOException;

/**
 * @author Alexander Hinze
 * @since 22/04/2024
 */
public final class FlexNodeDeserializer extends StdDeserializer<FlexNode> {
    public FlexNodeDeserializer() {
        super(FlexNode.class);
    }

    @Override
    public FlexNode deserialize(final JsonParser parser,
                                final DeserializationContext deserializationContext) throws IOException {
        final var node = parser.getCodec().readTree(parser);
        final var mapper = API.getObjectMapper();
        // @formatter:off
        return DefaultFlexNode.builder()
            .direction(FlexDirection.values()[((IntNode)node.get("direction")).intValue()])
            .overflow(FlexOverflow.values()[((IntNode)node.get("overflow")).intValue()])
            .positionType(FlexPositionType.values()[((IntNode)node.get("position_type")).intValue()])
            .alignSelf(FlexAlignment.values()[((IntNode)node.get("self_alignment")).intValue()])
            .alignItems(FlexAlignment.values()[((IntNode)node.get("item_alignment")).intValue()])
            .alignContent(FlexAlignment.values()[((IntNode)node.get("content_alignment")).intValue()])
            .justify(FlexJustify.values()[((IntNode)node.get("content_justification")).intValue()])
            .x(mapper.treeToValue(node.get("x"), FlexValue.class))
            .y(mapper.treeToValue(node.get("y"), FlexValue.class))
            .width(mapper.treeToValue(node.get("w"), FlexValue.class))
            .height(mapper.treeToValue(node.get("h"), FlexValue.class))
            .padding(mapper.treeToValue(node.get("padding"), FlexBorder.class))
            .margin(mapper.treeToValue(node.get("margin"), FlexBorder.class))
            .build();
        // @formatter:on
    }
}
