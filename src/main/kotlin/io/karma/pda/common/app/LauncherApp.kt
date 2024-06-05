/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.app

import io.karma.pda.api.common.app.AppType
import io.karma.pda.api.common.app.component.Spacer
import io.karma.pda.api.common.app.component.Spinner
import io.karma.pda.api.common.app.compose.*
import io.karma.pda.api.common.app.theme.font.DefaultFontFamilies
import io.karma.pda.api.common.app.theme.font.FontStyle
import io.karma.pda.api.common.color.Color
import io.karma.pda.api.common.color.GradientType
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
        DefaultView {
            Box({
                width(100.percent)
                height(100.percent)
                padding(4.px)
            }) {
                Text({
                    width(100.percent)
                    height(40.px)
                }) {
                    text uses this@LauncherApp.someSetting
                }

                Text({
                    width(50.percent)
                    height(20.px)
                }) {
                    text uses this@LauncherApp.someSetting
                    color(Color.GREEN)
                }

                Text({
                    width(75.percent)
                    height(20.px)
                }) {
                    text uses this@LauncherApp.someSetting
                }

                Box({
                    width(100.percent)
                    height(auto)
                    padding(4.px)
                }, props = { background(0x0A0A0A.rgb) }) {
                    Box({
                        width(100.percent)
                        height(auto)
                        direction(FlexDirection.ROW)
                        padding(4.px)
                    }, props = { background(0x141414.rgb) }) {
                        Text({
                            width(64.px)
                            height(64.px)
                            margin(4.px)
                        }) {
                            text("A")
                            color uses child<Spinner>("spinner").map { it!!.color }
                        }

                        Spacer({
                            width(20.px)
                        }) {
                            orientation(Spacer.Orientation.VERTICAL)
                            color uses child<Spinner>("spinner").map { it!!.color }
                        }

                        Text({
                            width(64.px)
                            height(64.px)
                            margin(4.px)
                        }) {
                            text("B")
                            color uses child<Spinner>("spinner").map { it!!.color }
                        }

                        Spacer({
                            width(20.px)
                        }) {
                            orientation(Spacer.Orientation.VERTICAL)
                            color uses child<Spinner>("spinner").map { it!!.color }
                        }

                        Text({
                            width(64.px)
                            height(64.px)
                            margin(4.px)
                        }) {
                            text("Hello, World!")
                            color uses child<Spinner>("spinner").map { it!!.color }
                        }

                        Spacer({
                            width(20.px)
                        }) {
                            orientation(Spacer.Orientation.VERTICAL)
                            color uses child<Spinner>("spinner").map { it!!.color }
                        }

                        Spinner({
                            width(64.px)
                            height(64.px)
                            margin(4.px)
                        }, localName = "spinner") {
                            color((Color.WHITE..Color.RED).gradient(GradientType.VERTICAL))
                        }
                    }
                }

                Text({
                    width(100.percent)
                    grow(1F)
                }) {
                    font(DefaultFontFamilies.NOTO_SANS.getFont(FontStyle.REGULAR, 24F))
                    text("Hello World, this is a text wrapping example on the PDA, finally WORKING \\o/")
                    color((Color.WHITE..Color.RED).gradient(GradientType.HORIZONTAL))
                }

                Text({
                    width(100.percent)
                    grow(1F)
                }) {
                    font(DefaultFontFamilies.NOTO_SANS.getFont(FontStyle.REGULAR, 20F))
                    text("Hello World, this is a text wrapping example on the PDA, finally WORKING \\o/")
                }

                Text({
                    width(100.percent)
                    grow(1F)
                }) {
                    font(DefaultFontFamilies.NOTO_SANS.getFont(FontStyle.REGULAR, 16F))
                    text("Hello World, this is a text wrapping example on the PDA, finally WORKING \\o/")
                }

                Text({
                    width(100.percent)
                    grow(1F)
                }) {
                    font(DefaultFontFamilies.NOTO_SANS.getFont(FontStyle.REGULAR, 12F))
                    text("Hello World, this is a text wrapping example on the PDA, finally WORKING \\o/")
                }
            }
        }
    }
}