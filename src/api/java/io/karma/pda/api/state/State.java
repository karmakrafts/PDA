/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.state;

import io.karma.pda.api.util.TypedValue;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * An instance of this interface may behave like a regular property;
 * its value can be set and get. But it also provides a unique ID, a
 * way to override said unique ID when needed, an accessor for the underlying
 * value type and a callback mechanism.
 * <p>
 * The callback mechanism allows instances of {@link StateHandler} to
 * inject itself into existing and most-importantly final fields which
 * are to be synchronized from client to server, and from server to
 * all remaining clients.
 *
 * @author Alexander Hinze
 * @since 11/04/2024
 */
public interface State<T> extends TypedValue<T> {
    // TODO: document this
    @Nullable
    String getStateKey();

    // TODO: document this
    void setStateKey(final String key);

    // TODO: document this
    BiConsumer<State<T>, T> getChangeCallback();

    /**
     * Retrieves the name of this property.
     *
     * @return The name of this property.
     */
    String getName();

    /**
     * Retrieves whether this property is saved to NBT or not.
     *
     * @return True if this property is saved to NBT.
     */
    boolean isPersistent();

    // TODO: document this
    void onChanged(final BiConsumer<State<T>, T> callback);

    // TODO: document this
    default <R> State<R> derive(final Class<R> type, final Function<T, R> mapper) {
        return new DerivedState<>(this, mapper, type);
    }
}
