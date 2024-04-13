/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.app

import io.karma.pda.api.common.app.AppType
import io.karma.pda.api.common.app.compose.ComposableApp
import io.karma.pda.api.common.app.theme.DefaultThemes

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
class SettingsApp(type: AppType<*>) : ComposableApp(type, DefaultThemes.DEFAULT_DARK) {
    override fun compose() {
        view("default") {}
    }
}