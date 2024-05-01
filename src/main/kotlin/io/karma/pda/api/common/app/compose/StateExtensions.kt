/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

@file:JvmName("ExtensionsKt")
@file:JvmMultifileClass

package io.karma.pda.api.common.app.compose

import io.karma.pda.api.common.state.State

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */

operator fun <T> State<T>.invoke(): T = get()