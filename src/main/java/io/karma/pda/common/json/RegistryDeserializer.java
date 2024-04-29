/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.json;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 29/04/2024
 */
public final class RegistryDeserializer<T> extends StdDeserializer<T> {
    private final Supplier<IForgeRegistry<T>> registrySupplier;

    public RegistryDeserializer(Class<?> type, final Supplier<IForgeRegistry<T>> registrySupplier) {
        super(type);
        this.registrySupplier = registrySupplier;
    }

    @Override
    public T deserialize(final JsonParser parser,
                         final DeserializationContext deserializationContext) throws IOException {
        final var name = ResourceLocation.tryParse(parser.getCodec().readValue(parser, String.class));
        if (name == null) {
            throw new JsonParseException(parser, "Could not parse registry name");
        }
        return registrySupplier.get().getValue(name);
    }
}
