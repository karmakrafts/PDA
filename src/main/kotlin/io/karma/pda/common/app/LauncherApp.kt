/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.app

import io.karma.pda.api.common.app.App
import io.karma.pda.api.common.app.AppType
import io.karma.pda.api.common.app.Launcher
import io.karma.pda.api.common.app.compose.*
import io.karma.pda.api.common.app.theme.DefaultThemes
import io.karma.pda.api.common.flex.FlexOverflow
import io.karma.pda.api.common.util.Color
import io.karma.sliced.slice.Slice
import java.util.*

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
class LauncherApp(type: AppType<*>) : ComposableApp(type, DefaultThemes.DEFAULT_DARK), Launcher {
    private val apps: Stack<App> = Stack()

    override fun compose() {
        view("default") {
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
            playerImage({
                width(100.percent)
                height(100.percent)
                overflow(FlexOverflow.HIDDEN)
            }) {
                player(UUID.fromString("b2ac8c03-d994-4805-9e0f-57fede63c04d"))
            }
        }
    }

    override fun closeApp(): App? {
        if (apps.isEmpty()) return null
        return apps.pop()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <A : App> openApp(type: AppType<A>): A {
        if (apps.isNotEmpty() && apps.peek().type == type) {
            return apps.peek() as A
        }
        return apps.push(type.create()) as A
    }

    override fun getActiveApps(): Slice<App> {
        return Slice.of(apps)
    }
}