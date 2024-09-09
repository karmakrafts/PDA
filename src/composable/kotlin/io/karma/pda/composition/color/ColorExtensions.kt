/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

@file:JvmName("ExtensionsKt")
@file:JvmMultifileClass

package io.karma.pda.composition.color

import io.karma.peregrine.api.color.Color
import io.karma.peregrine.api.color.Gradient
import io.karma.peregrine.api.color.GradientSampler

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */

inline val Int.rgb: Color
    get() = Color.unpackRGB(toInt())
inline val Int.rgba: Color
    get() = Color.unpackRGBA(toInt())
inline val Int.argb: Color
    get() = Color.unpackARGB(toInt())

inline val UInt.rgb: Color
    get() = Color.unpackRGB(toInt())
inline val UInt.rgba: Color
    get() = Color.unpackRGBA(toInt())
inline val UInt.argb: Color
    get() = Color.unpackARGB(toInt())

typealias ColorRange = Pair<Color, Color>

fun ColorRange.gradient(sampler: GradientSampler): Gradient =
    Gradient(first, second, sampler)

operator fun Color.rangeTo(other: Color): ColorRange =
    ColorRange(this, other)

operator fun Color.rangeUntil(other: Color): ColorRange =
    ColorRange(other, this)