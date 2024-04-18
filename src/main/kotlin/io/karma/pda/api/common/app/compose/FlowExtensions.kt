/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

@file:JvmName("ExtensionsKt")
@file:JvmMultifileClass
@file:OptIn(ExperimentalCoroutinesApi::class)

package io.karma.pda.api.common.app.compose

import io.karma.pda.api.common.sync.Synced
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

/**
 * @author Alexander Hinze
 * @since 18/04/2024
 */

inline fun <reified T> Flow<T?>.collectAsSynced(value: T? = null): Synced<T?> {
    val result = syncedOf(value)
    mapLatest {
        result.set(it) // Atomically update the value reference
        it
    }
    return result
}