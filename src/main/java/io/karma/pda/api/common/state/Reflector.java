/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.state;

import java.lang.annotation.*;

/**
 * @author Alexander Hinze
 * @since 27/04/2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Reflector {
    Class<? extends Annotation> value();
}
