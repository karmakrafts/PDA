/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author Alexander Hinze
 * @since 24/04/2024
 */
public final class TreeGraph<T> {
    private final T value;
    private final ArrayList<TreeGraph<T>> children = new ArrayList<>();

    public TreeGraph(final T value) {
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    public static <X, T extends X, R> TreeGraph<R> from(final X value, final Class<T> subType,
                                                        final Function<T, ? extends Collection<? extends X>> selector,
                                                        final Function<X, R> mapper) {
        final var result = new TreeGraph<>(mapper.apply(value));
        if (subType.isAssignableFrom(value.getClass())) {
            for (final var child : selector.apply((T) value)) {
                result.addChild(from(child, subType, selector, mapper));
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <X, T extends X> TreeGraph<X> from(final X value, final Class<T> subType,
                                                     final Function<T, ? extends Collection<? extends X>> selector) {
        final var result = new TreeGraph<>(value);
        if (subType.isAssignableFrom(value.getClass())) {
            for (final var child : selector.apply((T) value)) {
                result.addChild(from(child, subType, selector));
            }
        }
        return result;
    }

    public void addChild(final TreeGraph<T> child) {
        children.add(child);
    }

    public void removeChild(final TreeGraph<T> child) {
        children.remove(child);
    }

    public List<TreeGraph<T>> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public T getValue() {
        return value;
    }

    private void flattenRecursively(final TreeGraph<T> node, final ArrayList<T> values) {
        values.add(node.value);
        for (final var child : node.children) {
            flattenRecursively(child, values);
        }
    }

    public ArrayList<T> flatten() {
        final var result = new ArrayList<T>();
        flattenRecursively(this, result);
        return result;
    }
}
