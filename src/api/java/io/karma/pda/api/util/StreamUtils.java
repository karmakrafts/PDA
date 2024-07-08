/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.util;

import java.util.Iterator;
import java.util.Queue;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Alexander Hinze
 * @since 24/04/2024
 */
public final class StreamUtils {
    // @formatter:off
    private StreamUtils() {}
    // @formatter:on

    public static <T> Stream<T> consume(final Queue<T> queue) {
        return StreamSupport.stream(Spliterators.spliterator(new Iterator<>() {
            @Override
            public boolean hasNext() {
                return !queue.isEmpty();
            }

            @Override
            public T next() {
                return queue.poll();
            }
        }, queue.size(), 0), false);
    }
}
