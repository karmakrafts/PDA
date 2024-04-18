/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.sync;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a member-field to be
 * synchronized by an instance of {@link Synchronizer}
 * using the given codec type.
 * <p>
 * See {@link Synchronizer#register(Object)}.
 *
 * @author Alexander Hinze
 * @since 18/04/2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Sync {
    /**
     * Retrieves the type of codec to use for en- and decoding
     * the data for the annotated property field.
     *
     * @return The type of codec to use for en- and decoding the data for
     * the annotated property field.
     */
    Class<? extends SyncCodec<?>> value() default DefaultSyncCodec.class;
}
