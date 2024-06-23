/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.composition

import io.karma.pda.api.app.component.Component
import io.karma.pda.api.app.component.ComponentType
import io.karma.pda.api.app.component.Container
import io.karma.pda.api.util.Proxy
import java.util.*

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
@JvmInline
value class Composer<C : Container>(val container: C) {
    @ComposeDsl
    inline fun <T : Component> component(type: ComponentType<T>, crossinline layoutProps: LayoutProps): T {
        return type.create { builder -> builder.apply(layoutProps) }
    }

    @ComposeDsl
    inline fun <reified T : Component> child(localName: String): Proxy<T?> {
        return Proxy { container.findChildRecursively(localName) as? T }
    }

    @ComposeDsl
    inline fun <reified T : Component> child(id: UUID): Proxy<T?> {
        return Proxy { container.findChildRecursively(id) as? T }
    }

    @ComposeDsl
    inline fun <T : Component> component(
        type: ComponentType<T>,
        crossinline layoutProps: LayoutProps,
        localName: String? = null,
        props: @Composable T.() -> Unit = {}
    ) {
        val component = component(type = type, layoutProps = layoutProps)
        component.localName = localName
        component.apply(props)
        container.addChild(component)
    }

    @ComposeDsl
    inline fun <T : Container> container(
        type: ComponentType<T>,
        crossinline layoutProps: LayoutProps,
        localName: String? = null,
        props: @Composable T.() -> Unit = {},
        content: @Composable Composer<T>.() -> Unit
    ) {
        val component = component(type = type, layoutProps = layoutProps)
        component.localName = localName
        component.apply(props)
        Composer(component).apply(content)
        container.addChild(component)
    }
}