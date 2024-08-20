/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.dispose;

import java.util.Comparator;

/**
 * @author Alexander Hinze
 * @since 17/02/2024
 */
@FunctionalInterface
public interface Disposable {
    Comparator<Disposable> COMPARATOR = (a, b) -> Integer.compare(b.getDispositionPriority(),
        a.getDispositionPriority());

    void dispose();

    default int getDispositionPriority() {
        final var type = getClass();
        if (!type.isAnnotationPresent(DispositionPriority.class)) {
            return 0;
        }
        return type.getAnnotation(DispositionPriority.class).value();
    }
}
