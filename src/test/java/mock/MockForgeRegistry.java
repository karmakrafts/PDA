/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package mock;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.tags.ITagManager;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Alexander Hinze
 * @since 16/04/2024
 */
public final class MockForgeRegistry<V> implements IForgeRegistry<V>, HolderOwner<V> {
    private final ResourceLocation name;
    private final ResourceKey<Registry<V>> key;
    private final HashMap<ResourceLocation, V> entries = new HashMap<>();
    private final MockCodec<V> codec = new MockCodec<>(this);

    public MockForgeRegistry(final ResourceLocation name) {
        this.name = name;
        key = new MockResourceKey<>(new ResourceLocation("root"), name);
    }

    @Override
    public ResourceKey<Registry<V>> getRegistryKey() {
        return key;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return name;
    }

    @Override
    public void register(final String name, final V value) {
        register(ResourceLocation.tryParse(name), value);
    }

    @Override
    public void register(final ResourceLocation name, final V value) {
        if (entries.containsKey(name)) {
            throw new IllegalStateException(String.format("Entry '%s' is already registered", name));
        }
        entries.put(name, value);
    }

    @Override
    public boolean containsKey(final ResourceLocation name) {
        return entries.containsKey(name);
    }

    @Override
    public boolean containsValue(final V value) {
        return entries.containsValue(value);
    }

    @Override
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @Override
    public @Nullable V getValue(final ResourceLocation name) {
        return entries.get(name);
    }

    @Override
    public @Nullable ResourceLocation getKey(final V value) {
        final var entries = this.entries.entrySet();
        for (final var entry : entries) {
            if (!entry.getValue().equals(value)) {
                continue;
            }
            return entry.getKey();
        }
        return null;
    }

    @Override
    public @Nullable ResourceLocation getDefaultKey() {
        return null;
    }

    @Override
    public @NotNull Optional<ResourceKey<V>> getResourceKey(final V value) {
        final var name = getKey(value);
        if (name == null) {
            return Optional.empty();
        }
        return Optional.of(new MockResourceKey<>(this.name, name));
    }

    @Override
    public @NotNull Set<ResourceLocation> getKeys() {
        return entries.keySet();
    }

    @Override
    public @NotNull Collection<V> getValues() {
        return entries.values();
    }

    @Override
    public @NotNull Set<Map.Entry<ResourceKey<V>, V>> getEntries() { // @formatter:off
        return entries.values().stream()
            .map(value -> Pair.of(getResourceKey(value).orElseThrow(), value))
            .collect(Collectors.toSet());
    } // @formatter:on

    @Override
    public @NotNull Codec<V> getCodec() {
        return codec;
    }

    @Override
    public @NotNull Optional<Holder<V>> getHolder(final ResourceKey<V> key) {
        return Optional.of(new Holder.Direct<>(Objects.requireNonNull(getValue(key.location()))));
    }

    @Override
    public @NotNull Optional<Holder<V>> getHolder(final ResourceLocation name) {
        return Optional.of(new Holder.Direct<>(Objects.requireNonNull(getValue(name))));
    }

    @Override
    public @NotNull Optional<Holder<V>> getHolder(final V value) {
        return Optional.of(new Holder.Direct<>(Objects.requireNonNull(value)));
    }

    @Override
    public @Nullable ITagManager<V> tags() {
        return null;
    }

    @Override
    public @NotNull Optional<Holder.Reference<V>> getDelegate(final ResourceKey<V> key) {
        try {
            return Optional.of(getDelegateOrThrow(key));
        }
        catch (Throwable error) {
            return Optional.empty();
        }
    }

    @NotNull
    @Override
    public Holder.Reference<V> getDelegateOrThrow(final ResourceKey<V> key) {
        return Holder.Reference.createStandAlone(this, key);
    }

    @Override
    public @NotNull Optional<Holder.Reference<V>> getDelegate(final ResourceLocation name) {
        try {
            return Optional.of(getDelegateOrThrow(name));
        }
        catch (Throwable error) {
            return Optional.empty();
        }
    }

    @NotNull
    @Override
    public Holder.Reference<V> getDelegateOrThrow(final ResourceLocation name) {
        return Holder.Reference.createStandAlone(this, getResourceKey(getValue(name)).orElseThrow());
    }

    @Override
    public @NotNull Optional<Holder.Reference<V>> getDelegate(final V value) {
        try {
            return Optional.of(getDelegateOrThrow(value));
        }
        catch (Throwable error) {
            return Optional.empty();
        }
    }

    @NotNull
    @Override
    public Holder.Reference<V> getDelegateOrThrow(final V value) {
        return Holder.Reference.createStandAlone(this, getResourceKey(value).orElseThrow());
    }

    @Override
    public <T> T getSlaveMap(final ResourceLocation name, final Class<T> type) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Iterator<V> iterator() {
        return entries.values().iterator();
    }
}
