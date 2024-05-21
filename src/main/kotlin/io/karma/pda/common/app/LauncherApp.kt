/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.app

import io.karma.pda.api.common.app.AppType
import io.karma.pda.api.common.app.component.Separator
import io.karma.pda.api.common.app.component.Spinner
import io.karma.pda.api.common.app.compose.*
import io.karma.pda.api.common.color.Color
import io.karma.pda.api.common.color.GradientType
import io.karma.pda.api.common.flex.FlexBorder
import io.karma.pda.api.common.flex.FlexDirection
import io.karma.pda.api.common.state.MutableState
import io.karma.pda.api.common.state.Persistent
import io.karma.pda.api.common.state.Synchronize

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
@Composable
class LauncherApp(type: AppType<*>) : ComposableApp(type) {
    @Synchronize
    @Persistent
    private val someSetting: MutableState<String?> = mutableStateOf("Testing testing")

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
                    text by someSetting
                }

                label({
                    width(50.percent)
                    height(20.pixels)
                }, "testing") {
                    text by someSetting
                    color(Color.GREEN)
                }

                label({
                    width(75.percent)
                    height(20.pixels)
                }) {
                    text by someSetting
                }

                panel({
                    width(100.percent)
                    height(auto)
                    padding(FlexBorder.of(4.pixels))
                }) {
                    panel({
                        width(100.percent)
                        height(auto)
                        direction(FlexDirection.ROW)
                        padding(FlexBorder.of(4.pixels))
                    }) {
                        label({
                            width(32.pixels)
                            height(32.pixels)
                            margin(FlexBorder.of(4.pixels))
                        }) {
                            text("A")
                        }

                        separator({
                            width(20.pixels)
                        }) {
                            orientation(Separator.Orientation.VERTICAL)
                        }

                        label({
                            width(32.pixels)
                            height(32.pixels)
                            margin(FlexBorder.of(4.pixels))
                        }) {
                            text("B")
                        }

                        separator({
                            width(20.pixels)
                        }) {
                            orientation(Separator.Orientation.VERTICAL)
                        }

                        label({
                            width(32.pixels)
                            height(32.pixels)
                            margin(FlexBorder.of(4.pixels))
                        }) {
                            text("C")
                            color by childRef<Spinner>("spinner").map { it!!.color }
                        }

                        separator({
                            width(20.pixels)
                        }) {
                            orientation(Separator.Orientation.VERTICAL)
                            color by childRef<Spinner>("spinner").map { it!!.color }
                        }

                        spinner({
                            width(32.pixels)
                            height(32.pixels)
                            margin(FlexBorder.of(4.pixels))
                        }, "spinner") {
                            color((Color.WHITE..Color.RED).gradient(GradientType.VERTICAL))
                        }
                    }.apply {
                        background(0x141414.rgb)
                    }
                }.apply {
                    background(0x0A0A0A.rgb)
                }

                label({
                    width(100.percent)
                    grow(1F)
                }) {
                    text("- Testing 123 -")
                    color((Color.WHITE..Color.GREEN).gradient(GradientType.VERTICAL))
                }
            }
        }
    }
}