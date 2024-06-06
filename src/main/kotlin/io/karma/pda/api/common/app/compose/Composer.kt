/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.compose

import io.karma.pda.api.app.component.Component
import io.karma.pda.api.app.component.ComponentType
import io.karma.pda.api.app.component.Container
import io.karma.pda.api.flex.DefaultFlexNode
import io.karma.pda.api.util.Proxy
import io.karma.pda.foundation.component.*
import java.util.*

typealias LayoutProps = DefaultFlexNode.Builder.() -> Unit

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
@JvmInline
value class Composer<C : Container>(val container: C) : Container by container {
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

    @Composable
    inline fun DefaultContainer(
        crossinline layoutProps: LayoutProps,
        localName: String? = null,
        props: @Composable DefaultContainer.() -> Unit = {},
        content: @Composable Composer<DefaultContainer>.() -> Unit
    ) = container(
        type = DefaultComponents.CONTAINER,
        layoutProps = layoutProps,
        localName = localName,
        props = props,
        content = content
    )

    @Composable
    inline fun Box(
        crossinline layoutProps: LayoutProps,
        localName: String? = null,
        props: @Composable Box.() -> Unit = {},
        content: @Composable Composer<Box>.() -> Unit
    ) = container(
        type = DefaultComponents.BOX,
        layoutProps = layoutProps,
        localName = localName,
        props = props,
        content = content
    )

    @Composable
    inline fun Text(
        crossinline layoutProps: LayoutProps,
        localName: String? = null,
        props: @Composable Text.() -> Unit = {}
    ) = component(
        type = DefaultComponents.TEXT,
        layoutProps = layoutProps,
        localName = localName,
        props = props
    )

    @Composable
    inline fun Button(
        crossinline layoutProps: LayoutProps,
        localName: String? = null,
        props: @Composable Button.() -> Unit = {}
    ) = component(
        type = DefaultComponents.BUTTON,
        layoutProps = layoutProps,
        localName = localName,
        props = props
    )

    @Composable
    inline fun Spacer(
        crossinline layoutProps: LayoutProps,
        localName: String? = null,
        props: @Composable Spacer.() -> Unit = {}
    ) = component(
        type = DefaultComponents.SPACER,
        layoutProps = layoutProps,
        localName = localName,
        props = props
    )

    @Composable
    inline fun Image(
        crossinline layoutProps: LayoutProps,
        localName: String? = null,
        props: @Composable Image.() -> Unit = {}
    ) = component(
        type = DefaultComponents.IMAGE,
        layoutProps = layoutProps,
        localName = localName,
        props = props
    )

    @Composable
    inline fun ItemImage(
        crossinline layoutProps: LayoutProps,
        localName: String? = null,
        props: @Composable ItemImage.() -> Unit = {}
    ) = component(
        type = DefaultComponents.ITEM_IMAGE,
        layoutProps = layoutProps,
        localName = localName,
        props = props
    )

    @Composable
    inline fun BlockImage(
        crossinline layoutProps: LayoutProps,
        localName: String? = null,
        props: @Composable BlockImage.() -> Unit = {}
    ) = component(
        type = DefaultComponents.BLOCK_IMAGE,
        layoutProps = layoutProps,
        localName = localName,
        props = props
    )

    @Composable
    inline fun EntityImage(
        crossinline layoutProps: LayoutProps,
        localName: String? = null,
        props: @Composable EntityImage.() -> Unit = {}
    ) = component(
        type = DefaultComponents.ENTITY_IMAGE,
        layoutProps = layoutProps,
        localName = localName,
        props = props
    )

    @Composable
    inline fun RecipeImage(
        crossinline layoutProps: LayoutProps,
        localName: String? = null,
        props: @Composable RecipeImage.() -> Unit = {}
    ) = component(
        type = DefaultComponents.RECIPE_IMAGE,
        layoutProps = layoutProps,
        localName = localName,
        props = props
    )

    @Composable
    inline fun PlayerImage(
        crossinline layoutProps: LayoutProps,
        localName: String? = null,
        props: @Composable PlayerImage.() -> Unit = {}
    ) = component(
        type = DefaultComponents.PLAYER_IMAGE,
        layoutProps = layoutProps,
        localName = localName,
        props = props
    )

    @Composable
    inline fun Spinner(
        crossinline layoutProps: LayoutProps,
        localName: String? = null,
        props: @Composable Spinner.() -> Unit = {}
    ) = component(
        type = DefaultComponents.SPINNER,
        layoutProps = layoutProps,
        localName = localName,
        props = props
    )
}