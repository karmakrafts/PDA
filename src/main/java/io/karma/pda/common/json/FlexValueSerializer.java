/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.karma.pda.api.common.flex.FlexValue;

import java.io.IOException;

/**
 * @author Alexander Hinze
 * @since 21/04/2024
 */
public final class FlexValueSerializer extends StdSerializer<FlexValue> {
    public FlexValueSerializer() {
        super(FlexValue.class);
    }

    @Override
    public void serialize(final FlexValue value, final JsonGenerator generator,
                          final SerializerProvider serializerProvider) throws IOException {
        generator.writeStartObject();
        generator.writeNumberField("type", value.getType().ordinal());
        generator.writeNumberField("value", value.get());
        generator.writeEndObject();
    }
}
