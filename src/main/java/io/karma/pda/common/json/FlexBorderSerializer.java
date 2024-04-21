/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.karma.pda.api.common.flex.FlexBorder;

import java.io.IOException;

/**
 * @author Alexander Hinze
 * @since 21/04/2024
 */
public final class FlexBorderSerializer extends StdSerializer<FlexBorder> {
    public FlexBorderSerializer() {
        super(FlexBorder.class);
    }

    @Override
    public void serialize(final FlexBorder flexBorder, final JsonGenerator generator,
                          final SerializerProvider serializerProvider) throws IOException {
        generator.writeStartArray();
        generator.writeObject(flexBorder.getLeft());
        generator.writeObject(flexBorder.getTop());
        generator.writeObject(flexBorder.getRight());
        generator.writeObject(flexBorder.getBottom());
        generator.writeEndArray();
    }
}
