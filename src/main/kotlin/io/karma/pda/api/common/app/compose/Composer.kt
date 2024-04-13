/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.compose

import io.karma.pda.api.common.app.component.*
import io.karma.pda.api.common.flex.StaticFlexNode

typealias ComponentProps = StaticFlexNode.Builder.() -> Unit

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

    inline fun itemRender(
        crossinline props: ComponentProps, scope: ItemRender.() -> Unit
    ) = component(DefaultComponents.ITEM_RENDER, props, scope)

    inline fun blockRender(
        crossinline props: ComponentProps, scope: BlockRender.() -> Unit
    ) = component(DefaultComponents.BLOCK_RENDER, props, scope)

    inline fun entityRender(
        crossinline props: ComponentProps, scope: EntityRender.() -> Unit
    ) = component(DefaultComponents.ENTITY_RENDER, props, scope)

    inline fun recipeRender(
        crossinline props: ComponentProps, scope: RecipeRender.() -> Unit
    ) = component(DefaultComponents.RECIPE_RENDER, props, scope)

    inline fun playerRender(
        crossinline props: ComponentProps, scope: PlayerRender.() -> Unit
    ) = component(DefaultComponents.PLAYER_RENDER, props, scope)
}