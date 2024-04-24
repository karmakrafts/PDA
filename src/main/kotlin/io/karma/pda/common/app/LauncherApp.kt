/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.app

import io.karma.pda.api.common.app.AppType
import io.karma.pda.api.common.app.compose.*
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
                padding(FlexBorder.of(4.pixels))
            }) {
                label({
                    width(100.percent)
                    height(40.pixels)
                }) {
                    text("Testing 1")
                }

                label({
                    width(50.percent)
                    height(20.pixels)
                }) {
                    text("Testing 2")
                }

                label({
                    width(75.percent)
                    height(20.pixels)
                }) {
                    text("Testing 3")
                }

                panel({
                    width(100.percent)
                    height(auto)
                    padding(FlexBorder.of(4.pixels))
                    grow(1F)
                }) {
                    panel({
                        width(100.percent)
                        height(auto)
                        direction(FlexDirection.ROW)
                        padding(FlexBorder.of(4.pixels))
                        grow(1F)
                    }) {
                        label({
                            width(32.pixels)
                            height(32.pixels)
                            margin(FlexBorder.of(4.pixels))
                        }) {
                            text("A")
                        }

                        label({
                            width(32.pixels)
                            height(32.pixels)
                            margin(FlexBorder.of(8.pixels))
                        }) {
                            text("B")
                        }

                        label({
                            width(32.pixels)
                            height(32.pixels)
                            margin(FlexBorder.of(12.pixels))
                        }) {
                            text("C")
                        }

                        label({
                            width(32.pixels)
                            height(32.pixels)
                            margin(FlexBorder.of(16.pixels))
                        }) {
                            text("D")
                        }
                    }
                }
            }
        }
    }
}