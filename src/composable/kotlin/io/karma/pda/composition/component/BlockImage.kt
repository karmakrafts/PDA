/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

@file:JvmName("ComponentExtensionsKt")
@file:JvmMultifileClass

package io.karma.pda.composition.component

import io.karma.pda.api.app.component.Container
import io.karma.pda.composition.Composable
import io.karma.pda.composition.Composer
import io.karma.pda.composition.LayoutProps
import io.karma.pda.foundation.component.BlockImage
import io.karma.pda.foundation.component.DefaultComponents

/**
 * @author Alexander Hinze
 * @since 07/06/2024
 */

@Composable
inline fun <C : Container> Composer<C>.BlockImage(
    crossinline layoutProps: LayoutProps,
    localName: String? = null,
    props: @Composable BlockImage.() -> Unit = {}
) = component(
    type = DefaultComponents.BLOCK_IMAGE,
    layoutProps = layoutProps,
    localName = localName,
    props = props
)