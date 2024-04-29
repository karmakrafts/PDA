/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.karma.pda.api.common.API;
import io.karma.pda.api.common.color.GradientFunction;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 29/04/2024
 */
public final class GradientFunctionDeserializer extends StdDeserializer<GradientFunction> {
    public GradientFunctionDeserializer() {
        super(GradientFunction.class);
    }

    @Override
    public GradientFunction deserialize(final JsonParser parser,
                                        final DeserializationContext deserializationContext) throws IOException {
        final var nameString = parser.getCodec().readValue(parser, String.class);
        final var name = Objects.requireNonNull(ResourceLocation.tryParse(nameString));
        return API.getGradientFunctionRegistry().getValue(name);
    }
}
