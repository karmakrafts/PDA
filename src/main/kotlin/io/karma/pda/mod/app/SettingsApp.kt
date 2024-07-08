/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.app

import io.karma.pda.composition.Composable
import io.karma.pda.composition.app.ComposableApp

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
@io.karma.pda.composition.Composable
class SettingsApp(type: io.karma.pda.api.app.AppType<*>) : ComposableApp(type) {
    override fun compose() {
        DefaultView {}
    }
}