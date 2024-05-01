/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

@file:JvmName("ExtensionsKt")
@file:JvmMultifileClass

package io.karma.pda.api.common.app.compose

import io.karma.pda.api.common.color.Color
import io.karma.pda.api.common.state.MutableState
import io.karma.pda.api.common.state.State
import java.util.function.Supplier

/**
 * @author Alexander Hinze
 * @since 26/04/2024
 */

operator fun <T : Any?> MutableState<T>.invoke(value: T) = set(value)

operator fun MutableState<Color>.invoke(value: Int) = set(
    Color.unpackARGB(value)
)

operator fun MutableState<Color>.invoke(value: UInt) = set(
    Color.unpackARGB(value.toInt())
)

inline fun <reified T : Any?> mutableStateOf(value: T? = null): MutableState<T?> {
    return if (value == null) MutableState.ofNull(T::class.java)
    else MutableState.of(value)
}

infix fun <T : Any?> MutableState<T>.by(state: State<out T>): MutableState<T> {
    setBy(state)
    return this
}

inline infix fun <T : Any?> MutableState<T>.by(crossinline stateProvider: () -> State<out T>): MutableState<T> {
    setBy { stateProvider() }
    return this
}

infix fun <T : Any?> MutableState<T>.by(stateProvider: Supplier<out State<out T>>): MutableState<T> {
    setBy(stateProvider)
    return this
}