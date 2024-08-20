/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.dispose;

import java.util.List;

/**
 * @author Alexander Hinze
 * @since 21/08/2024
 */
public interface DispositionHandler {
    void register(final Disposable disposable);

    void unregister(final Disposable disposable);

    List<Disposable> getObjects();

    void disposeAll();
}
