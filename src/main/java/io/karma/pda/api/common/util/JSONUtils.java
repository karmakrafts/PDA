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
import io.karma.pda.api.common.API;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
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

    public static byte[] compress(final JsonNode node) {
        try (final var stream = new ByteArrayOutputStream(); final var compressedStream = new LZ4BlockOutputStream(
            stream)) {
            WRITER.writeValue(compressedStream, node);
            return stream.toByteArray();
        }
        catch (Throwable error) {
            API.getLogger().error("Could not compress JSON node: {}", error.getMessage());
            return new byte[0];
        }
    }

    public static <T> @Nullable T decompress(final byte[] data, final Class<T> type) {
        try (final var stream = new ByteArrayInputStream(data); final var decompressedStream = new LZ4BlockInputStream(
            stream)) {
            return READER.readValue(decompressedStream, type);
        }
        catch (Throwable error) {
            API.getLogger().error("Could not decompress JSON node: {}", error.getMessage());
            return null;
        }
    }
}