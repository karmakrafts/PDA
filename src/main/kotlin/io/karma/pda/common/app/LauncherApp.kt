/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.app

import io.karma.pda.api.common.app.AppContext
import io.karma.pda.api.common.app.AppType
import io.karma.pda.api.common.app.compose.ComposableApp
import io.karma.pda.api.common.app.compose.auto
import io.karma.pda.api.common.app.compose.percent
import io.karma.pda.api.common.app.compose.pixels
import io.karma.pda.api.common.app.compose.*
import io.karma.pda.api.common.flex.FlexOverflow
import io.karma.pda.api.common.util.Color
import java.util.*

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
class LauncherApp(type: AppType<*>) : ComposableApp(type) {
    override fun init(context: AppContext) {
        compose {
            defaultContainer({
                width(100.percent)
                height(100.percent)
            }) {
                label({
                    width(100.percent)
                    height(auto)
                }) {
                    text("Testing composable apps on PDA")
                    color(Color.BLACK)
                }
                button({
                    width(120.pixels)
                    height(20.pixels)
                }) {
                    onClicked { println("HELLO WORLD!") }
                }
                playerRender({
                    width(100.percent)
                    height(100.percent)
                    overflow(FlexOverflow.HIDDEN)
                }) {
                    player(UUID.fromString("b2ac8c03-d994-4805-9e0f-57fede63c04d"))
                }
            }
        }
    }
}