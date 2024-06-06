/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.util;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 18/04/2024
 */
public interface TypedValue<T> extends Supplier<T> {
    /**
     * Retrieves the type of the value contained
     * within this property, regardless whether it's null or not.
     *
     * @return The type of the value contained within this property.
     */
    Class<T> getType();
}
