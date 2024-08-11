/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.app

import io.karma.pda.api.app.AppType
import io.karma.pda.api.state.MutableState
import io.karma.pda.api.state.Persistent
import io.karma.pda.api.state.Synchronize
import io.karma.pda.composition.Composable
import io.karma.pda.composition.app.ComposableApp
import io.karma.pda.composition.component.Box
import io.karma.pda.composition.flex.percent
import io.karma.pda.composition.flex.px
import io.karma.pda.composition.state.mutableStateOf

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
                width(50.percent)
                height(50.percent)
                padding(4.px)
            }) {
                /*
                Text({
                    width(100.percent)
                    height(40.px)
                }) {
                    text uses this@LauncherApp.someSetting
                    color uses text.derive {
                        if (it.contains("Testing")) Color.BLACK
                        else Color.WHITE
                    }
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
                            color(
                                (Color.WHITE..Color.RED).gradient(GradientType.VERTICAL)
                            )
                        }
                    }
                }

                Text({
                    width(100.percent)
                    grow(1F)
                }) {
                    font(
                        DefaultFontFamilies.NOTO_SANS.getFont(
                            FontStyle.REGULAR, 24F
                        )
                    )
                    text("Hello World, this is a text wrapping example on the PDA, finally WORKING \\o/")
                    color(
                        (Color.WHITE..Color.RED).gradient(
                            GradientType.HORIZONTAL
                        )
                    )
                }

                Text({
                    width(100.percent)
                    grow(1F)
                }) {
                    font(
                        DefaultFontFamilies.NOTO_SANS.getFont(
                            FontStyle.REGULAR, 20F
                        )
                    )
                    text("Hello World, this is a text wrapping example on the PDA, finally WORKING \\o/")
                }

                Text({
                    width(100.percent)
                    grow(1F)
                }) {
                    font(
                        DefaultFontFamilies.NOTO_SANS.getFont(
                            FontStyle.REGULAR, 16F
                        )
                    )
                    text("Hello World, this is a text wrapping example on the PDA, finally WORKING \\o/")
                }

                Text({
                    width(100.percent)
                    grow(1F)
                }) {
                    font(
                        DefaultFontFamilies.NOTO_SANS.getFont(
                            FontStyle.REGULAR, 12F
                        )
                    )
                    text("Hello World, this is a text wrapping example on the PDA, finally WORKING \\o/")
                }
                 */
            }
        }
    }
}