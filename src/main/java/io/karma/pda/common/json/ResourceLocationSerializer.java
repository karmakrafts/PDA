/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

/**
 * @author Alexander Hinze
 * @since 29/04/2024
 */
public final class ResourceLocationSerializer extends StdSerializer<ResourceLocation> {
    public ResourceLocationSerializer() {
        super(ResourceLocation.class);
    }

    @Override
    public void serialize(final ResourceLocation location, final JsonGenerator generator,
                          final SerializerProvider serializerProvider) throws IOException {
        generator.writeString(location.toString());
    }
}
