/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

@file:JvmName("ExtensionsKt")
@file:JvmMultifileClass
@file:OptIn(ExperimentalCoroutinesApi::class)

package io.karma.pda.composition

import io.karma.pda.api.state.State
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

/**
 * @author Alexander Hinze
 * @since 18/04/2024
 */

@Composable
inline fun <reified T> Flow<T?>.collectAsState(value: T? = null): State<T?> {
    val result = mutableStateOf(value)
    mapLatest {
        result.set(it) // Atomically update the value reference
        it
    }
    return result
}