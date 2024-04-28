/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.karma.pda.api.common.color.GradientFunction;

import java.io.IOException;

/**
 * @author Alexander Hinze
 * @since 29/04/2024
 */
public final class GradientFunctionSerializer extends StdSerializer<GradientFunction> {
    public GradientFunctionSerializer() {
        super(GradientFunction.class);
    }

    @Override
    public void serialize(final GradientFunction gradientFunction, final JsonGenerator generator,
                          final SerializerProvider serializerProvider) throws IOException {
        generator.writeString(gradientFunction.getName().toString());
    }
}
