/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.util;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.karma.pda.api.common.API;
import io.karma.pda.api.common.flex.FlexNode;
import io.karma.pda.api.common.flex.FlexValue;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author Alexander Hinze
 * @since 12/02/2024
 */
public final class JSONUtils {
    public static final ObjectMapper MAPPER;
    public static final ObjectReader READER;
    public static final ObjectWriter WRITER;

    static { // @formatter:off
        MAPPER = new ObjectMapper();
        READER = MAPPER.reader();
        WRITER = MAPPER.writer(new DefaultPrettyPrinter()
            .withSeparators(Separators.createDefaultInstance().withRootSeparator("\n")));
    } // @formatter:on

    // @formatter:off
    private JSONUtils() {}
    // @formatter:on

    public static <T> ObjectNode encodeObject(final Class<T> type, final @Nullable T value) {
        final var node = MAPPER.createObjectNode();
        node.put("type", type.getName());
        if (value != null) {
            node.set("value", MAPPER.valueToTree(value));
        }
        else {
            node.putNull("value");
        }
        return node;
    }

    @SuppressWarnings("unchecked")
    public static ObjectNode encodeObject(final Object value) {
        return encodeObject((Class<Object>) value.getClass(), value);
    }

    @SuppressWarnings("unchecked")
    public static <T> @Nullable Pair<Class<T>, @Nullable T> decodeObject(final ObjectNode node) {
        final var rawValue = node.get("value");
        if (rawValue == null || rawValue.isNull()) {
            return null;
        }
        try {
            final var type = (Class<T>) Class.forName(node.get("type").asText());
            return Pair.of(type, MAPPER.treeToValue(rawValue, type));
        }
        catch (Throwable error) {
            API.getLogger().error("Could not convert object node to class instance: {}", error.getMessage());
            return null;
        }
    }

    public static byte[] compressRaw(final JsonNode value) {
        try (final var stream = new ByteArrayOutputStream(); final var compressedStream = new LZ4BlockOutputStream(
            stream)) {
            WRITER.writeValue(compressedStream, value);
            return stream.toByteArray();
        }
        catch (Throwable error) {
            API.getLogger().error("Could not compress JSON node: {}", error.getMessage());
            return new byte[0];
        }
    }

    public static byte[] compress(final Object value) {
        try (final var stream = new ByteArrayOutputStream(); final var compressedStream = new LZ4BlockOutputStream(
            stream)) {
            WRITER.writeValue(compressedStream, encodeObject(value));
            return stream.toByteArray();
        }
        catch (Throwable error) {
            API.getLogger().error("Could not compress JSON node: {}", error.getMessage());
            return new byte[0];
        }
    }

    public static <T> @Nullable T decompressRaw(final byte[] data, final Class<T> type) {
        try (final var stream = new ByteArrayInputStream(data); final var decompressedStream = new LZ4BlockInputStream(
            stream)) {
            return READER.readValue(decompressedStream, type);
        }
        catch (Throwable error) {
            API.getLogger().error("Could not decompress JSON node: {}", error.getMessage());
            return null;
        }
    }

    public static <T> @Nullable Pair<Class<T>, @Nullable T> decompress(final byte[] data) {
        try (final var stream = new ByteArrayInputStream(data); final var decompressedStream = new LZ4BlockInputStream(
            stream)) {
            return decodeObject(READER.readValue(decompressedStream, ObjectNode.class));
        }
        catch (Throwable error) {
            API.getLogger().error("Could not decompress JSON node: {}", error.getMessage());
            return null;
        }
    }
}