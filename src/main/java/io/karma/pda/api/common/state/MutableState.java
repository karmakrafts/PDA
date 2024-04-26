/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.state;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 26/04/2024
 */
public interface MutableState<T> extends State<T> {
    static <T> MutableState<T> fromPair(final Pair<Class<T>, T> pair) {
        final var value = pair.getRight();
        if (value == null) {
            return ofNull(pair.getLeft());
        }
        return of(value);
    }

    /**
     * Creates a new synchronized property instance with an initial value of null and the given type.
     * The ID for this property will be generated randomly and synchronized automatically.
     *
     * @param type An instance of the underlying value's type.
     * @param <T>  The type of the newly created property.
     * @return A new synchronized property instance with the given type.
     */
    static <T> MutableState<T> ofNull(final Class<T> type) {
        return new DefaultState<>(type, null);
    }

    /**
     * Creates a new synchronized property instance with the given initial value.
     * The ID for this property will be generated randomly and synchronized automatically.
     * The type of the property is derived from the given value.
     *
     * @param value The value to set the property to initially.
     * @param <T>   The type of the newly created property.
     * @return A new synchronized property instance with the given initial value.
     */
    @SuppressWarnings("unchecked")
    static <T> MutableState<T> of(final T value) {
        return new DefaultState<>((Class<T>) value.getClass(), value);
    }

    /**
     * Creates a new synchronized property instance whose instance
     * is provided by the given supplier function. The property created
     * by this function is immutable and cannot be set directly.
     * A call to {@link MutableState#set(Object)} will throw an {@link UnsupportedOperationException}.
     *
     * @param type     The type of the property to create.
     * @param delegate The function which to call when retrieving the value of the property.
     * @param <T>      The type of the newly created property.
     * @return A new synchronized property instance with the given delegate function.
     */
    static <T> MutableState<T> by(final Class<T> type, final Supplier<T> delegate) {
        return new DelegateState<>(type, delegate);
    }

    /**
     * Sets the value contained within this synchronized property.
     *
     * @param value The value to set this property to.
     */
    void set(final @Nullable T value);

    /**
     * Sets the name of this property.
     *
     * @param name The name of this property.
     */
    void setName(final String name);

    /**
     * Sets a flag which determines whether to save this
     * property to the NBT snapshot once the session is
     * terminated or not.
     *
     * @param isPersistent True if the property should be saved to NBT for persistence.
     */
    void setPersistent(final boolean isPersistent);

    /**
     * Sets the callback invoked by this property upon
     * being set.
     *
     * @param callback The callback invoked by this property upon being set.
     */
    void setCallback(final @Nullable BiConsumer<State<T>, T> callback);
}
