/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.dispose;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 17/02/2024
 */
public final class DispositionHandler {
    private final Consumer<Disposable> callback;
    private final ConcurrentLinkedQueue<Disposable> objects = new ConcurrentLinkedQueue<>();
    private final Set<AutoCloseable> delegatedObjects = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public DispositionHandler(final Consumer<Disposable> callback) {
        this.callback = callback;
    }

    public DispositionHandler() {
        this(Disposable::dispose);
    }

    public void addObject(final AutoCloseable object) {
        if (delegatedObjects.contains(object)) {
            return;
        }
        objects.add(new DisposableDelegate(object));
        delegatedObjects.add(object);
    }

    public void addObject(final Disposable object) {
        if (objects.contains(object)) {
            return;
        }
        objects.add(object);
    }

    public void disposeAll() {
        while (!objects.isEmpty()) {
            callback.accept(objects.remove());
        }
        delegatedObjects.clear();
    }

    private record DisposableDelegate(AutoCloseable closeable) implements Disposable {
        @SuppressWarnings("all")
        @Override
        public void dispose() {
            try {
                closeable.close();
            }
            catch (final Exception error) {
                error.fillInStackTrace().printStackTrace();
            }
        }
    }
}
