/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.data;

import io.karma.pda.api.common.app.component.Component;
import io.karma.pda.api.common.util.JSONUtils;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Alexander Hinze
 * @since 18/02/2024
 */
public final class ComponentCodec {
    // @formatter:off
    private ComponentCodec() {}
    // @formatter:on

    @SuppressWarnings("all")
    public static byte[] encode(final Component component) {
        try (final var stream = new ByteArrayOutputStream(); final var compressedStream = new GZIPOutputStream(stream)) {
            JSONUtils.WRITER.writeValue(compressedStream, component);
            return stream.toByteArray();
        }
        catch (final Exception error) {
            error.fillInStackTrace().printStackTrace();
            return new byte[0];
        }
    }

    @SuppressWarnings("all")
    public static <C extends Component> @Nullable C decode(final byte[] data, final Class<C> type) {
        try (final var stream = new ByteArrayInputStream(data); final var compressedStream = new GZIPInputStream(stream)) {
            return JSONUtils.READER.readValue(compressedStream, type);
        }
        catch (final Exception error) {
            error.fillInStackTrace().printStackTrace();
            return null;
        }
    }
}
