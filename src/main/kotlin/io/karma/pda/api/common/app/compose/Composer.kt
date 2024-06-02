/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.compose

import io.karma.pda.api.common.app.component.*
import io.karma.pda.api.common.flex.DefaultFlexNode
import io.karma.pda.api.common.util.Proxy
import java.util.*

typealias ComponentProps = DefaultFlexNode.Builder.() -> Unit

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
@JvmInline
value class Composer<C : Container>(val container: C) {
    @ComposeDsl
    inline fun <T : Component> component(type: ComponentType<T>, crossinline props: ComponentProps): T {
        return type.create { builder -> builder.apply(props) }
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
        crossinline props: ComponentProps,
        localName: String? = null,
        scope: @Composable T.() -> Unit = {}
    ): T {
        val component = component(type, props)
        component.localName = localName
        component.apply(scope)
        container.addChild(component)
        return component
    }

    @ComposeDsl
    inline fun <T : Container> container(
        type: ComponentType<T>,
        crossinline props: ComponentProps,
        localName: String? = null,
        scope: @Composable Composer<T>.() -> Unit = {}
    ): T {
        val component = component(type, props)
        component.localName = localName
        Composer(component).apply(scope)
        container.addChild(component)
        return component
    }

    @Composable
    inline fun DefaultContainer(
        crossinline props: ComponentProps,
        localName: String? = null,
        scope: @Composable Composer<DefaultContainer>.() -> Unit = {}
    ): DefaultContainer = container(DefaultComponents.CONTAINER, props, localName, scope)

    @Composable
    inline fun Box(
        crossinline props: ComponentProps,
        localName: String? = null,
        scope: @Composable Composer<Box>.() -> Unit = {}
    ): Box = container(DefaultComponents.BOX, props, localName, scope)

    @Composable
    inline fun Text(
        crossinline props: ComponentProps, localName: String? = null, scope: @Composable Text.() -> Unit = {}
    ): Text = component(DefaultComponents.TEXT, props, localName, scope)

    @Composable
    inline fun Button(
        crossinline props: ComponentProps, localName: String? = null, scope: @Composable Button.() -> Unit = {}
    ): Button = component(DefaultComponents.BUTTON, props, localName, scope)

    @Composable
    inline fun Spacer(
        crossinline props: ComponentProps, localName: String? = null, scope: @Composable Spacer.() -> Unit = {}
    ): Spacer = component(DefaultComponents.SPACER, props, localName, scope)

    @Composable
    inline fun Image(
        crossinline props: ComponentProps, localName: String? = null, scope: @Composable Image.() -> Unit = {}
    ): Image = component(DefaultComponents.IMAGE, props, localName, scope)

    @Composable
    inline fun ItemImage(
        crossinline props: ComponentProps, localName: String? = null, scope: @Composable ItemImage.() -> Unit = {}
    ): ItemImage = component(DefaultComponents.ITEM_IMAGE, props, localName, scope)

    @Composable
    inline fun BlockImage(
        crossinline props: ComponentProps, localName: String? = null, scope: @Composable BlockImage.() -> Unit = {}
    ): BlockImage = component(DefaultComponents.BLOCK_IMAGE, props, localName, scope)

    @Composable
    inline fun EntityImage(
        crossinline props: ComponentProps,
        localName: String? = null,
        scope: @Composable EntityImage.() -> Unit = {}
    ): EntityImage = component(DefaultComponents.ENTITY_IMAGE, props, localName, scope)

    @Composable
    inline fun RecipeImage(
        crossinline props: ComponentProps,
        localName: String? = null,
        scope: @Composable RecipeImage.() -> Unit = {}
    ): RecipeImage = component(DefaultComponents.RECIPE_IMAGE, props, localName, scope)

    @Composable
    inline fun PlayerImage(
        crossinline props: ComponentProps,
        localName: String? = null,
        scope: @Composable PlayerImage.() -> Unit = {}
    ): PlayerImage = component(DefaultComponents.PLAYER_IMAGE, props, localName, scope)

    @Composable
    inline fun Spinner(
        crossinline props: ComponentProps, localName: String? = null, scope: @Composable Spinner.() -> Unit = {}
    ): Spinner = component(DefaultComponents.SPINNER, props, localName, scope)
}