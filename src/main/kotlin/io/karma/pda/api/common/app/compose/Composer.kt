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
    inline fun <T : Component> makeComponent(type: ComponentType<T>, crossinline props: ComponentProps): T {
        return type.create { builder -> builder.apply(props) }
    }

    inline fun <reified T : Component> child(localName: String): T? {
        return container.findChildRecursively(localName) as? T
    }

    inline fun <reified T : Component> childRef(localName: String): Proxy<T?> {
        return Proxy { child<T>(localName) }
    }

    inline fun <reified T : Component> child(id: UUID): T? {
        return container.findChildRecursively(id) as? T
    }

    inline fun <reified T : Component> childRef(id: UUID): Proxy<T?> {
        return Proxy { child<T>(id) }
    }

    inline fun <T : Component> component(
        type: ComponentType<T>, crossinline props: ComponentProps, localName: String? = null, scope: T.() -> Unit = {}
    ): T {
        val component = makeComponent(type, props)
        component.localName = localName
        component.apply(scope)
        container.addChild(component)
        return component
    }

    inline fun <T : Container> container(
        type: ComponentType<T>,
        crossinline props: ComponentProps,
        localName: String? = null,
        scope: Composer<T>.() -> Unit = {}
    ): T {
        val component = makeComponent(type, props)
        component.localName = localName
        Composer(component).apply(scope)
        container.addChild(component)
        return component
    }

    inline fun defaultContainer(
        crossinline props: ComponentProps, localName: String? = null, scope: Composer<DefaultContainer>.() -> Unit = {}
    ): DefaultContainer = container(DefaultComponents.CONTAINER, props, localName, scope)

    inline fun panel(
        crossinline props: ComponentProps, localName: String? = null, scope: Composer<Panel>.() -> Unit = {}
    ): Panel = container(DefaultComponents.PANEL, props, localName, scope)

    inline fun label(
        crossinline props: ComponentProps, localName: String? = null, scope: Label.() -> Unit = {}
    ): Label = component(DefaultComponents.LABEL, props, localName, scope)

    inline fun button(
        crossinline props: ComponentProps, localName: String? = null, scope: Button.() -> Unit = {}
    ): Button = component(DefaultComponents.BUTTON, props, localName, scope)

    inline fun separator(
        crossinline props: ComponentProps, localName: String? = null, scope: Separator.() -> Unit = {}
    ): Separator = component(DefaultComponents.SEPARATOR, props, localName, scope)

    inline fun image(
        crossinline props: ComponentProps, localName: String? = null, scope: Image.() -> Unit = {}
    ): Image = component(DefaultComponents.IMAGE, props, localName, scope)

    inline fun itemImage(
        crossinline props: ComponentProps, localName: String? = null, scope: ItemImage.() -> Unit = {}
    ): ItemImage = component(DefaultComponents.ITEM_IMAGE, props, localName, scope)

    inline fun blockImage(
        crossinline props: ComponentProps, localName: String? = null, scope: BlockImage.() -> Unit = {}
    ): BlockImage = component(DefaultComponents.BLOCK_IMAGE, props, localName, scope)

    inline fun entityImage(
        crossinline props: ComponentProps, localName: String? = null, scope: EntityImage.() -> Unit = {}
    ): EntityImage = component(DefaultComponents.ENTITY_IMAGE, props, localName, scope)

    inline fun recipeImage(
        crossinline props: ComponentProps, localName: String? = null, scope: RecipeImage.() -> Unit = {}
    ): RecipeImage = component(DefaultComponents.RECIPE_IMAGE, props, localName, scope)

    inline fun playerImage(
        crossinline props: ComponentProps, localName: String? = null, scope: PlayerImage.() -> Unit = {}
    ): PlayerImage = component(DefaultComponents.PLAYER_IMAGE, props, localName, scope)

    inline fun spinner(
        crossinline props: ComponentProps, localName: String? = null, scope: Spinner.() -> Unit = {}
    ): Spinner = component(DefaultComponents.SPINNER, props, localName, scope)
}