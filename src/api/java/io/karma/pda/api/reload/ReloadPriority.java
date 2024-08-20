/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.reload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Alexander Hinze
 * @since 21/08/2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ReloadPriority {
    int value();
}
