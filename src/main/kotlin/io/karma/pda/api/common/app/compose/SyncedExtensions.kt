/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

@file:JvmName("ExtensionsKt")
@file:JvmMultifileClass

package io.karma.pda.api.common.app.compose

import io.karma.pda.api.common.sync.Synced
import io.karma.pda.api.common.util.Color

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */

operator fun <T> Synced<T>.invoke(): T = get()
operator fun <T> Synced<T>.invoke(value: T) = set(value)

operator fun Synced<Color>.invoke(value: Int) = set(
    Color.unpackARGB(value))
operator fun Synced<Color>.invoke(value: UInt) = set(
    Color.unpackARGB(value.toInt()))