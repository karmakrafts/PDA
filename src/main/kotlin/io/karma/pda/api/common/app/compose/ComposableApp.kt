/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.compose

import io.karma.pda.api.app.AbstractApp
import io.karma.pda.api.app.AppType
import io.karma.pda.foundation.component.DefaultContainer
import io.karma.pda.foundation.view.DefaultContainerView

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
@Composable
abstract class ComposableApp(type: AppType<*>) : AbstractApp(type) {
    @Composable
    abstract override fun compose()

    @Composable
    @Suppress("FunctionName")
    protected inline fun View(name: String, crossinline scope: @Composable Composer<DefaultContainer>.() -> Unit) {
        addView(name, DefaultContainerView(name) { container ->
            Composer(container).apply(scope)
        })
    }

    @Composable
    @Suppress("FunctionName")
    protected inline fun DefaultView(crossinline scope: @Composable Composer<DefaultContainer>.() -> Unit) {
        View(DEFAULT_VIEW, scope)
    }
}