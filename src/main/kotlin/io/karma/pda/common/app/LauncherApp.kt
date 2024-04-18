/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.app

import io.karma.pda.api.common.app.AppType
import io.karma.pda.api.common.app.compose.*
import io.karma.pda.common.PDAMod

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
class LauncherApp(type: AppType<*>) : ComposableApp(type) {
    override fun compose() {
        PDAMod.LOGGER.debug("Composing launcher app")
        defaultView {
            PDAMod.LOGGER.debug("Composing default view")

            label({
                width(100.percent)
                height(20.pixels)
            }) {
                text("Hello World!")
            }
        }
    }
}