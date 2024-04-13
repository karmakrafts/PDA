/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.compose

import io.karma.pda.api.common.app.component.*
import io.karma.pda.api.common.flex.StaticFlexNode

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
@JvmInline
value class Composer<C : Container>(val container: C) {
    inline fun <T : Component> makeComponent(type: ComponentType<T>, props: StaticFlexNode.Builder.() -> Unit): T {
        val builder = StaticFlexNode.builder()
        builder.apply(props)
        val component = type.create()
        component.flexNode.setFrom(builder.build())
        return component
    }

    inline fun <T : Component> component(
        type: ComponentType<T>, props: StaticFlexNode.Builder.() -> Unit, scope: T.() -> Unit
    ) {
        val component = makeComponent(type, props)
        component.apply(scope)
        container.addChild(component)
    }

    inline fun <T : Container> container(
        type: ComponentType<T>, props: StaticFlexNode.Builder.() -> Unit, scope: Composer<T>.() -> Unit
    ) {
        val component = makeComponent(type, props)
        Composer(component).apply(scope)
        container.addChild(component)
    }

    inline fun defaultContainer(
        props: StaticFlexNode.Builder.() -> Unit, scope: Composer<DefaultContainer>.() -> Unit
    ) = container(DefaultComponents.CONTAINER, props, scope)

    inline fun label(
        props: StaticFlexNode.Builder.() -> Unit, scope: Label.() -> Unit
    ) = component(DefaultComponents.LABEL, props, scope)

    inline fun button(
        props: StaticFlexNode.Builder.() -> Unit, scope: Button.() -> Unit
    ) = component(DefaultComponents.BUTTON, props, scope)

    inline fun separator(
        props: StaticFlexNode.Builder.() -> Unit, scope: Separator.() -> Unit
    ) = component(DefaultComponents.SEPARATOR, props, scope)

    inline fun image(
        props: StaticFlexNode.Builder.() -> Unit, scope: Image.() -> Unit
    ) = component(DefaultComponents.IMAGE, props, scope)

    inline fun itemRender(
        props: StaticFlexNode.Builder.() -> Unit, scope: ItemRender.() -> Unit
    ) = component(DefaultComponents.ITEM_RENDER, props, scope)

    inline fun blockRender(
        props: StaticFlexNode.Builder.() -> Unit, scope: BlockRender.() -> Unit
    ) = component(DefaultComponents.BLOCK_RENDER, props, scope)

    inline fun entityRender(
        props: StaticFlexNode.Builder.() -> Unit, scope: EntityRender.() -> Unit
    ) = component(DefaultComponents.ENTITY_RENDER, props, scope)

    inline fun recipeRender(
        props: StaticFlexNode.Builder.() -> Unit, scope: RecipeRender.() -> Unit
    ) = component(DefaultComponents.RECIPE_RENDER, props, scope)

    inline fun playerRender(
        props: StaticFlexNode.Builder.() -> Unit, scope: PlayerRender.() -> Unit
    ) = component(DefaultComponents.PLAYER_RENDER, props, scope)
}