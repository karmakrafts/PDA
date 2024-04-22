/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.compose

import io.karma.pda.api.common.app.component.*
import io.karma.pda.api.common.flex.DefaultFlexNode

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

    inline fun <T : Component> component(
        type: ComponentType<T>, crossinline props: ComponentProps, scope: T.() -> Unit
    ) {
        val component = makeComponent(type, props)
        component.apply(scope)
        container.addChild(component)
    }

    inline fun <T : Container> container(
        type: ComponentType<T>, crossinline props: ComponentProps, scope: Composer<T>.() -> Unit
    ) {
        val component = makeComponent(type, props)
        Composer(component).apply(scope)
        container.addChild(component)
    }

    inline fun defaultContainer(
        crossinline props: ComponentProps, scope: Composer<DefaultContainer>.() -> Unit
    ) = container(DefaultComponents.CONTAINER, props, scope)

    inline fun panel(
        crossinline props: ComponentProps, scope: Composer<Panel>.() -> Unit
    ) = container(DefaultComponents.PANEL, props, scope)

    inline fun label(
        crossinline props: ComponentProps, scope: Label.() -> Unit
    ) = component(DefaultComponents.LABEL, props, scope)

    inline fun button(
        crossinline props: ComponentProps, scope: Button.() -> Unit
    ) = component(DefaultComponents.BUTTON, props, scope)

    inline fun separator(
        crossinline props: ComponentProps, scope: Separator.() -> Unit
    ) = component(DefaultComponents.SEPARATOR, props, scope)

    inline fun image(
        crossinline props: ComponentProps, scope: Image.() -> Unit
    ) = component(DefaultComponents.IMAGE, props, scope)

    inline fun itemImage(
        crossinline props: ComponentProps, scope: ItemImage.() -> Unit
    ) = component(DefaultComponents.ITEM_IMAGE, props, scope)

    inline fun blockImage(
        crossinline props: ComponentProps, scope: BlockImage.() -> Unit
    ) = component(DefaultComponents.BLOCK_IMAGE, props, scope)

    inline fun entityImage(
        crossinline props: ComponentProps, scope: EntityImage.() -> Unit
    ) = component(DefaultComponents.ENTITY_IMAGE, props, scope)

    inline fun recipeImage(
        crossinline props: ComponentProps, scope: RecipeImage.() -> Unit
    ) = component(DefaultComponents.RECIPE_IMAGE, props, scope)

    inline fun playerImage(
        crossinline props: ComponentProps, scope: PlayerImage.() -> Unit
    ) = component(DefaultComponents.PLAYER_IMAGE, props, scope)
}