/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.app

import io.karma.pda.api.common.app.AppType
import io.karma.pda.api.common.app.compose.ComposableApp
import io.karma.pda.api.common.app.compose.percent
import io.karma.pda.api.common.app.compose.pixels
import io.karma.pda.api.common.flex.FlexBorder
import io.karma.pda.api.common.flex.FlexDirection

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
class LauncherApp(type: AppType<*>) : ComposableApp(type) {
    override fun compose() {
        defaultView {
            panel({
                width(100.percent)
                height(100.percent)
            }) {
                label({
                    width(100.percent)
                    height(40.pixels)
                }) {}

                label({
                    width(50.percent)
                    height(20.pixels)
                }) {}

                label({
                    width(75.percent)
                    height(20.pixels)
                }) {}

                panel({
                    width(100.percent)
                    height(100.percent)
                    padding(FlexBorder.of(8.pixels))
                    direction(FlexDirection.ROW)
                }) {
                    label({
                        width(32.pixels)
                        height(32.pixels)
                        margin(FlexBorder.of(2.pixels))
                    }) {}

                    label({
                        width(32.pixels)
                        height(32.pixels)
                        margin(FlexBorder.of(4.pixels))
                    }) {}

                    label({
                        width(32.pixels)
                        height(32.pixels)
                        margin(FlexBorder.of(6.pixels))
                    }) {}

                    label({
                        width(32.pixels)
                        height(32.pixels)
                        margin(FlexBorder.of(8.pixels))
                    }) {}
                }
            }
        }
    }
}