/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

@file:JvmName("ExtensionsKt")
@file:JvmMultifileClass

package io.karma.pda.api.common.app.compose

import io.karma.pda.api.common.color.Color
import io.karma.pda.api.common.state.MutableState

/**
 * @author Alexander Hinze
 * @since 26/04/2024
 */

operator fun <T> MutableState<T>.invoke(value: T) = set(value)

operator fun MutableState<Color>.invoke(value: Int) = set(
    Color.unpackARGB(value)
)

operator fun MutableState<Color>.invoke(value: UInt) = set(
    Color.unpackARGB(value.toInt())
)

inline fun <reified T> mutableStateOf(value: T? = null): MutableState<T?> {
    return if (value == null) MutableState.ofNull(T::class.java)
    else MutableState.of(value)
}

inline fun <reified T> mutableStateBy(crossinline function: () -> T?): MutableState<T?> {
    return MutableState.by(T::class.java) { function() }
}