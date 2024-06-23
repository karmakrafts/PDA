/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

@file:JvmName("ComponentExtensionsKt")
@file:JvmMultifileClass

package io.karma.pda.composition.component

import io.karma.pda.api.app.component.Component
import io.karma.pda.api.util.Proxy
import io.karma.pda.composition.ComposeDsl
import java.util.*

/**
 * @author Alexander Hinze
 * @since 02/06/2024
 */

@ComposeDsl
inline fun <reified T : Component> Component.child(localName: String): Proxy<T?> {
    return Proxy { parent?.findChildRecursively(localName) as? T }
}

@ComposeDsl
inline fun <reified T : Component> Component.child(id: UUID): Proxy<T?> {
    return Proxy { parent?.findChildRecursively(id) as? T }
}