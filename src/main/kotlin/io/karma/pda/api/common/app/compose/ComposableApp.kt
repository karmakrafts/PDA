/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.compose

import io.karma.pda.api.common.app.AbstractApp
import io.karma.pda.api.common.app.AppType
import io.karma.pda.api.common.app.component.DefaultContainer

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
abstract class ComposableApp(type: AppType<*>) : AbstractApp(type) {
    protected fun compose(scope: Composer<DefaultContainer>.() -> Unit) {
        Composer(container).apply(scope)
    }
}