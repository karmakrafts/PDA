/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.compose

import io.karma.pda.api.common.color.Color
import io.karma.pda.api.common.color.Gradient
import io.karma.pda.api.common.color.GradientFunction

/**
 * @author Alexander Hinze
 * @since 29/04/2024
 */
data class ColorRange(val start: Color, val end: Color) {
    fun gradient(function: GradientFunction): Gradient {
        return Gradient(start, end, function)
    }
}