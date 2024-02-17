package io.karma.pda.common.util;

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
