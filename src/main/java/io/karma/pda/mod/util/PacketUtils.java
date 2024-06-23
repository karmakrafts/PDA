/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.util;

import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Alexander Hinze
 * @since 24/04/2024
 */
public final class PacketUtils {
    // @formatter:off
    private PacketUtils() {}
    // @formatter:on

    public static <T> void writeNullable(final @Nullable T value, final BiConsumer<FriendlyByteBuf, T> writer,
                                         final FriendlyByteBuf buffer) {
        if (value == null) {
            buffer.writeBoolean(false);
            return;
        }
        buffer.writeBoolean(true);
        writer.accept(buffer, value);
    }

    public static <T> @Nullable T readNullable(final FriendlyByteBuf buffer,
                                               final Function<FriendlyByteBuf, T> reader) {
        if (!buffer.readBoolean()) {
            return null;
        }
        return reader.apply(buffer);
    }

    public static <K, V> void writeMap(final Map<K, V> map, final BiConsumer<FriendlyByteBuf, K> keyWriter,
                                       final BiConsumer<FriendlyByteBuf, V> valueWriter, final FriendlyByteBuf buffer) {
        buffer.writeVarInt(map.size());
        for (final var entry : map.entrySet()) {
            keyWriter.accept(buffer, entry.getKey());
            valueWriter.accept(buffer, entry.getValue());
        }
    }

    public static <K, V> Map<K, V> readMap(final FriendlyByteBuf buffer, final Function<FriendlyByteBuf, K> keyReader,
                                           final Function<FriendlyByteBuf, V> valueReader) {
        final var numValues = buffer.readVarInt();
        if (numValues == 0) {
            return Collections.emptyMap();
        }
        final var map = new HashMap<K, V>(numValues);
        for (var i = 0; i < numValues; i++) {
            map.put(keyReader.apply(buffer), valueReader.apply(buffer));
        }
        return map;
    }

    public static <T> void writeList(final List<T> values, final BiConsumer<FriendlyByteBuf, T> writer,
                                     final FriendlyByteBuf buffer) {
        final var numValues = values.size();
        buffer.writeVarInt(numValues);
        for (var i = 0; i < numValues; i++) {
            writer.accept(buffer, values.get(i));
        }
    }

    public static <T> List<T> readList(final FriendlyByteBuf buffer, final Function<FriendlyByteBuf, T> reader) {
        final var numValues = buffer.readVarInt();
        if (numValues == 0) {
            return Collections.emptyList();
        }
        final var values = new ArrayList<T>(numValues);
        for (var i = 0; i < numValues; i++) {
            values.add(reader.apply(buffer));
        }
        return values;
    }
}
