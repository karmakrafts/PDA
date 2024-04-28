/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

@file:JvmName("ExtensionsKt")
@file:JvmMultifileClass

package io.karma.pda.api.common.app.compose

import io.karma.pda.api.common.util.Color

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

// Int conversions
fun Int.unpackRGB(): Color = Color.unpackRGB(this)
fun Int.unpackRGBA(): Color = Color.unpackRGBA(this)
fun Int.unpackARGB(): Color = Color.unpackARGB(this)

// UInt conversions
fun UInt.unpackRGB(): Color = Color.unpackRGB(toInt())
fun UInt.unpackRGBA(): Color = Color.unpackRGBA(toInt())
fun UInt.unpackARGB(): Color = Color.unpackARGB(toInt())