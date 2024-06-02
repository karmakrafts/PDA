/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.compose

import io.karma.pda.api.common.app.component.Component
import io.karma.pda.api.common.util.Proxy
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