/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.color;

import io.karma.pda.api.util.RectangleCorner;

/**
 * @author Alexander Hinze
 * @since 29/04/2024
 */
@FunctionalInterface
public interface AnonGradientFunction {
    Color remap(final Color start, final Color end, final RectangleCorner corner);
}
