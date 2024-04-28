/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.util;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 28/04/2024
 */
public final class MutableLazy<T> implements Supplier<T> {
    private final Supplier<T> supplier;
    private T value;

    public MutableLazy(final Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public void set(final T value) {
        this.value = value;
    }

    public @Nullable T getUnsafe() {
        return value;
    }

    @Override
    public T get() {
        if (value == null) {
            value = supplier.get();
        }
        return value;
    }
}
