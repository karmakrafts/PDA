/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

/**
 * @author Alexander Hinze
 * @since 29/04/2024
 */
public final class ResourceLocationDeserializer extends StdDeserializer<ResourceLocation> {
    public ResourceLocationDeserializer() {
        super(ResourceLocation.class);
    }

    @Override
    public ResourceLocation deserialize(final JsonParser parser,
                                        final DeserializationContext deserializationContext) throws IOException {
        return ResourceLocation.tryParse(parser.getCodec().readValue(parser, String.class));
    }
}
