/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

@file:JvmName("ExtensionsKt")
@file:JvmMultifileClass

package io.karma.pda.api.common.app.compose

import io.karma.pda.api.flex.FlexValue

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */

inline val auto: FlexValue
    get() = FlexValue.auto()

// Int conversions
inline val Int.px: FlexValue
    get() = FlexValue.pixel(this)
inline val Int.percent: FlexValue
    get() = FlexValue.percent(this.toFloat())

// Float conversions
inline val Float.px: FlexValue
    get() = FlexValue.pixel(this.toInt())
inline val Float.percent: FlexValue
    get() = FlexValue.percent(this)

// Double conversion
inline val Double.px: FlexValue
    get() = FlexValue.pixel(this.toInt())
inline val Double.percent: FlexValue
    get() = FlexValue.percent(this.toFloat())