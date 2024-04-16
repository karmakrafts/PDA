/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package mock;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 16/04/2024
 */
public final class MockCodec<T> implements Codec<T> {
    private final IForgeRegistry<T> registry;

    public MockCodec(final IForgeRegistry<T> registry) {
        this.registry = registry;
    }

    @Override
    public <T1> DataResult<Pair<T, T1>> decode(final DynamicOps<T1> dynamicOps, final T1 t1) {
        final var name = ResourceLocation.tryParse(dynamicOps.getStringValue(t1).get().left().orElseThrow());
        return DataResult.success(Pair.of(registry.getValue(name), t1));
    }

    @Override
    public <T1> DataResult<T1> encode(final T t, final DynamicOps<T1> dynamicOps, final T1 t1) {
        dynamicOps.createString(Objects.requireNonNull(registry.getKey(t)).toString());
        return DataResult.success(t1);
    }
}
