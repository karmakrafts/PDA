/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.app

import io.karma.pda.api.app.AppType
import io.karma.pda.composition.Composable
import io.karma.pda.composition.app.ComposableApp

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
@Composable
class SettingsApp(type: AppType<*>) : ComposableApp(type) {
    override fun compose() {
        DefaultView {}
    }
}