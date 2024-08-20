/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.dispose;

import io.karma.pda.api.dispose.Disposable;
import io.karma.pda.api.dispose.DispositionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 17/02/2024
 */
public final class DefaultDispositionHandler implements DispositionHandler {
    private final Consumer<Disposable> callback;
    private final ConcurrentLinkedQueue<Disposable> objects = new ConcurrentLinkedQueue<>();

    public DefaultDispositionHandler(final Consumer<Disposable> callback) {
        this.callback = callback;
    }

    @Override
    public void register(final Disposable disposable) {
        if (objects.contains(disposable)) {
            return;
        }
        objects.add(disposable);
    }

    @Override
    public void unregister(final Disposable disposable) {
        objects.remove(disposable);
    }

    @Override
    public List<Disposable> getObjects() {
        final var sorted = new ArrayList<>(objects);
        sorted.sort(Disposable.COMPARATOR);
        return sorted;
    }

    @Override
    public void disposeAll() {
        getObjects().forEach(callback);
        objects.clear();
    }
}
