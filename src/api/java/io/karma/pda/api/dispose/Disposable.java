/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.dispose;

import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 17/02/2024
 */
@FunctionalInterface
public interface Disposable extends Comparable<Disposable> {
    void dispose();

    default int getDispositionPriority() {
        return 0;
    }

    @Override
    default int compareTo(@NotNull Disposable o) {
        return Integer.compare(o.getDispositionPriority(), getDispositionPriority());
    }
}
