/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.util;

import io.karma.pda.mod.PDAMod;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author Alexander Hinze
 * @since 14/04/2024
 */
public final class BlockingHashMap<K, V> implements Map<K, V> {
    private final ConcurrentHashMap<K, LinkedBlockingQueue<V>> delegate = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    private BlockingQueue<V> getQueue(final Object key) {
        return delegate.computeIfAbsent((K) key, k -> new LinkedBlockingQueue<>());
    }

    public CompletableFuture<@Nullable V> removeLater(final @Nullable K key, final Executor executor) {
        return CompletableFuture.supplyAsync(() -> remove(key), executor);
    }

    public CompletableFuture<@Nullable V> removeLater(final @Nullable K key, final long timeout,
                                                      final TimeUnit timeUnit, final Executor executor) {
        return CompletableFuture.supplyAsync(() -> remove(key, timeout, timeUnit), executor);
    }

    public CompletableFuture<@Nullable V> removeLater(final @Nullable K key) {
        return CompletableFuture.supplyAsync(() -> remove(key));
    }

    public CompletableFuture<@Nullable V> removeLater(final @Nullable K key, final long timeout,
                                                      final TimeUnit timeUnit) {
        return CompletableFuture.supplyAsync(() -> remove(key, timeout, timeUnit));
    }

    public @Nullable V remove(final @Nullable K key, final long timeout, final TimeUnit timeUnit) {
        try {
            final var value = getQueue(key).poll(timeout, timeUnit);
            if (value != null && key != null) {
                delegate.remove(key); // Remove the queue itself too
            }
            return value;
        }
        catch (InterruptedException e) {
            PDAMod.LOGGER.error("Interruped while removing value from the queue: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public int size() {
        var size = 0;
        for (final var queue : delegate.values()) {
            if (queue.isEmpty()) {
                continue;
            }
            size++;
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        for (final var queue : delegate.values()) {
            if (queue.isEmpty()) {
                continue;
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean containsKey(final Object key) {
        return !getQueue(key).isEmpty();
    }

    @Override
    public boolean containsValue(final @Nullable Object value) {
        if (value == null) {
            return false;
        }
        for (final var queue : delegate.values()) {
            if (!queue.contains(value)) {
                continue;
            }
            return true;
        }
        return false;
    }

    @Override
    public V get(final Object key) {
        return getQueue(key).peek();
    }

    @SuppressWarnings("all")
    @Nullable
    @Override
    public V put(final K key, final V value) {
        final var queue = getQueue(key);
        final var oldValue = queue.poll();
        queue.add(value);
        return oldValue;
    }

    @Override
    public V remove(final Object key) {
        try {
            final var value = getQueue(key).take();
            if (key != null) {
                delegate.remove(key);
            }
            return value;
        }
        catch (InterruptedException e) {
            PDAMod.LOGGER.error("Interruped while removing value from the queue: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void putAll(final @NotNull Map<? extends K, ? extends V> map) {
        final var entries = map.entrySet();
        for (final var entry : entries) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @NotNull
    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }

    @NotNull
    @Override
    public Collection<V> values() {
        return delegate.values().stream().map(BlockingQueue::peek).toList();
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() { // @formatter:off
        return delegate.entrySet().stream()
            .map(entry -> Pair.of(entry.getKey(), entry.getValue().peek()))
            .collect(Collectors.toSet());
    } // @formatter:on
}
