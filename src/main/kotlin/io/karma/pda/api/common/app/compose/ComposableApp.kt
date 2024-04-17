/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.compose

import io.karma.pda.api.common.app.AbstractApp
import io.karma.pda.api.common.app.AppType
import io.karma.pda.api.common.app.component.DefaultContainer
import io.karma.pda.api.common.app.view.DefaultContainerView

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
abstract class ComposableApp(type: AppType<*>) : AbstractApp(type) {
    abstract fun compose()

    override fun init() {
        compose()
        super.init()
    }

    protected inline fun view(name: String, crossinline scope: Composer<DefaultContainer>.() -> Unit) {
        addView(name, DefaultContainerView(name) { container ->
            Composer(container).apply(scope)
        })
    }

    protected inline fun defaultView(crossinline scope: Composer<DefaultContainer>.() -> Unit) {
        view(DEFAULT_VIEW, scope)
    }
}