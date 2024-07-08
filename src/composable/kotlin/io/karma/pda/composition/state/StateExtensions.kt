/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

@file:JvmName("StateExtensionsKt")
@file:JvmMultifileClass

package io.karma.pda.composition.state

import io.karma.pda.api.state.State

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */

operator fun <T> State<T>.invoke(): T = get()

inline infix fun <reified R, T> State<T>.derive(crossinline function: (T) -> R): State<R> {
    return derive(R::class.java) { function(it) }
}