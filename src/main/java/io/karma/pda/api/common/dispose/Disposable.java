/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.dispose;

/**
 * @author Alexander Hinze
 * @since 17/02/2024
 */
public interface Disposable extends AutoCloseable {
    void dispose();

    @Override
    default void close() {
        dispose();
    }
}
