/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.app

import io.karma.pda.api.common.app.AppContext
import io.karma.pda.api.common.app.AppType
import io.karma.pda.api.common.app.compose.*
import io.karma.pda.api.common.app.theme.DefaultThemes
import io.karma.pda.common.PDAMod

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
class LauncherApp(type: AppType<*>) : ComposableApp(type, DefaultThemes.DEFAULT_DARK) {
    override fun compose(context: AppContext) {
        PDAMod.LOGGER.debug("Composing launcher app")
        defaultView {
            PDAMod.LOGGER.debug("Composing default view")

            label({}) {
                text("Hello World!")
            }
        }
    }
}