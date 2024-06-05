/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.theme;

import io.karma.material.dynamiccolor.DynamicScheme;
import io.karma.material.hct.Hct;

/**
 * @author Alexander Hinze
 * @since 03/06/2024
 */
@FunctionalInterface
public interface SchemeFactory<S extends DynamicScheme> {
    S create(final Hct hct, final boolean isDark, final double contrast);
}
