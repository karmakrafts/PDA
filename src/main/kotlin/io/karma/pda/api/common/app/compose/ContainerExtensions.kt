/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

@file:JvmName("ExtensionsKt")
@file:JvmMultifileClass

package io.karma.pda.api.common.app.compose

import io.karma.pda.api.app.component.Component
import io.karma.pda.api.app.component.Container
import io.karma.pda.api.util.Proxy
import java.util.*

/**
 * @author Alexander Hinze
 * @since 02/06/2024
 */

@ComposeDsl
inline fun <reified T : Component> Container.child(localName: String): Proxy<T?> {
    return Proxy { findChildRecursively(localName) as? T }
}

@ComposeDsl
inline fun <reified T : Component> Container.child(id: UUID): Proxy<T?> {
    return Proxy { findChildRecursively(id) as? T }
}