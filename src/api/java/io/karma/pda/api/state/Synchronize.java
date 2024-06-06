/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.state;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a member-field to be
 * synchronized by an instance of {@link StateHandler}.
 *
 * @author Alexander Hinze
 * @since 18/04/2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Synchronize {
}
