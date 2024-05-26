/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.app

import io.karma.pda.api.common.app.AppType
import io.karma.pda.api.common.app.component.Separator
import io.karma.pda.api.common.app.component.Spinner
import io.karma.pda.api.common.app.compose.*
import io.karma.pda.api.common.app.theme.font.DefaultFontFamilies
import io.karma.pda.api.common.app.theme.font.FontStyle
import io.karma.pda.api.common.app.theme.font.FontVariant
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
                            width(64.pixels)
                            height(64.pixels)
                            margin(FlexBorder.of(4.pixels))
                        }) {
                            text("A")
                            color by childRef<Spinner>("spinner").map { it!!.color }
                        }

                        separator({
                            width(20.pixels)
                        }) {
                            orientation(Separator.Orientation.VERTICAL)
                            color by childRef<Spinner>("spinner").map { it!!.color }
                        }

                        label({
                            width(64.pixels)
                            height(64.pixels)
                            margin(FlexBorder.of(4.pixels))
                        }) {
                            text("B")
                            color by childRef<Spinner>("spinner").map { it!!.color }
                        }

                        separator({
                            width(20.pixels)
                        }) {
                            orientation(Separator.Orientation.VERTICAL)
                            color by childRef<Spinner>("spinner").map { it!!.color }
                        }

                        label({
                            width(64.pixels)
                            height(64.pixels)
                            margin(FlexBorder.of(4.pixels))
                        }) {
                            text("Hello, World!")
                            color by childRef<Spinner>("spinner").map { it!!.color }
                        }

                        separator({
                            width(20.pixels)
                        }) {
                            orientation(Separator.Orientation.VERTICAL)
                            color by childRef<Spinner>("spinner").map { it!!.color }
                        }

                        spinner({
                            width(64.pixels)
                            height(64.pixels)
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
                    font(DefaultFontFamilies.NOTO_SANS.getFont(FontStyle.REGULAR, 24F))
                    text("Hello World, this is a text wrapping example on the PDA, finally WORKING \\o/")
                }
            }
        }
    }
}