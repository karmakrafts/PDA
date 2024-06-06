/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.state;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 26/04/2024
 */
public interface MutableState<T> extends State<T>, Consumer<T> {
    // TODO: document this
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
     * Sets the value contained within this synchronized property.
     *
     * @param value The value to set this property to.
     */
    void set(final Supplier<@Nullable T> value);

    // TODO: document this
    default void set(final @Nullable T value) {
        set(() -> value);
    }

    /**
     * Sets the name of this property.
     *
     * @param name The name of this property.
     */
    @ApiStatus.Internal
    void setName(final String name);

    /**
     * Sets a flag which determines whether to save this
     * property to the NBT snapshot once the session is
     * terminated or not.
     *
     * @param isPersistent True if the property should be saved to NBT for persistence.
     */
    void setPersistent(final boolean isPersistent);

    // TODO: document this
    default void setBy(final State<? extends T> state) {
        state.onChanged((prop, value) -> set(value));
    }

    // TODO: document this
    default void setBy(final Supplier<? extends State<? extends T>> stateProvider) {
        set(() -> stateProvider.get().get());
    }

    @Override
    default void accept(final @Nullable T value) {
        set(value);
    }
}
