/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

@file:JvmName("StateExtensionsKt")
@file:JvmMultifileClass

package io.karma.pda.composition.state

import io.karma.pda.api.color.Color
import io.karma.pda.api.state.MutableState
import io.karma.pda.api.state.State
import io.karma.pda.composition.ComposeDsl
import java.util.function.Supplier
import kotlin.reflect.KProperty

/**
 * @author Alexander Hinze
 * @since 26/04/2024
 */

operator fun <T : Any?> MutableState<T>.invoke(value: T) = set(value)
operator fun <T : Any?> MutableState<T>.invoke(): T = get()

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

@ComposeDsl
infix fun <T : Any?> MutableState<T>.uses(state: State<out T>): MutableState<T> {
    setBy(state)
    return this
}

@ComposeDsl
inline infix fun <T : Any?> MutableState<T>.uses(crossinline stateProvider: () -> State<out T>): MutableState<T> {
    setBy { stateProvider() }
    return this
}

@ComposeDsl
infix fun <T : Any?> MutableState<T>.uses(stateProvider: Supplier<out State<out T>>): MutableState<T> {
    setBy(stateProvider)
    return this
}

operator fun <T : Any?> MutableState<T>.getValue(thisRef: Any?, property: KProperty<*>): T? = get()
operator fun <T : Any?> MutableState<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T?) = set(value)