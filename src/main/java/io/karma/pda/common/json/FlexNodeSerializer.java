/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.karma.pda.api.common.flex.FlexNode;

import java.io.IOException;

/**
 * @author Alexander Hinze
 * @since 22/04/2024
 */
public final class FlexNodeSerializer extends StdSerializer<FlexNode> {
    public FlexNodeSerializer() {
        super(FlexNode.class);
    }

    @Override
    public void serialize(final FlexNode flexNode, final JsonGenerator generator,
                          final SerializerProvider serializerProvider) throws IOException {
        generator.writeStartObject();
        generator.writeNumberField("direction", flexNode.getDirection().ordinal());
        generator.writeNumberField("overflow", flexNode.getOverflow().ordinal());
        generator.writeNumberField("position_type", flexNode.getPositionType().ordinal());
        generator.writeNumberField("self_alignment", flexNode.getSelfAlignment().ordinal());
        generator.writeNumberField("item_alignment", flexNode.getItemAlignment().ordinal());
        generator.writeNumberField("content_alignment", flexNode.getContentAlignment().ordinal());
        generator.writeNumberField("content_justification", flexNode.getContentJustification().ordinal());
        generator.writeObjectField("x", flexNode.getX());
        generator.writeObjectField("y", flexNode.getY());
        generator.writeObjectField("w", flexNode.getWidth());
        generator.writeObjectField("h", flexNode.getHeight());
        generator.writeObjectField("padding", flexNode.getPadding());
        generator.writeObjectField("margin", flexNode.getMargin());
        generator.writeEndObject();
    }
}
