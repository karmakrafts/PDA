/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.sync;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Alexander Hinze
 * @since 18/04/2024
 */
public interface SyncCodec<T> extends SyncEncodeFunction<T>, SyncDecodeFunction<T> {
    Class<T> getType();

    static <T> SyncCodec<T> create(final Class<T> type, final SyncEncodeFunction<T> encoder,
                                   final SyncDecodeFunction<T> decoder) {
        return new SyncCodec<>() {
            @Override
            public void encode(final String name, final Class<T> type, final T value, final ObjectNode node) {
                encoder.encode(name, type, value, node);
            }

            @Override
            public T decode(final String name, final ObjectNode node) {
                return decoder.decode(name, node);
            }

            @Override
            public Class<T> getType() {
                return type;
            }
        };
    }
}
