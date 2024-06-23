/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import net.minecraftforge.registries.IForgeRegistry;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 29/04/2024
 */
public final class RegistrySerializer<T> extends StdSerializer<T> {
    private final Supplier<IForgeRegistry<T>> registrySupplier;

    @SuppressWarnings("unchecked")
    public RegistrySerializer(final Class<?> type, final Supplier<IForgeRegistry<T>> registrySupplier) {
        super((Class<T>) type);
        this.registrySupplier = registrySupplier;
    }

    @Override
    public void serialize(final T value, final JsonGenerator generator,
                          final SerializerProvider serializerProvider) throws IOException {
        generator.writeString(Objects.requireNonNull(registrySupplier.get().getKey(value)).toString());
    }
}
