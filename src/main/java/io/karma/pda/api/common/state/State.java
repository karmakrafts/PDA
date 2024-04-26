/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.state;

import io.karma.pda.api.common.util.TypedValue;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

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
    /**
     * Retrieves the callback invoked by this property
     * upon being set if present.
     *
     * @return The callback invoke by this property upon being set if present.
     */
    @Nullable
    BiConsumer<State<T>, T> getCallback();

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
}
