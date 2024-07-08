/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.app

import io.karma.pda.mod.app.LauncherApp
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

/**
 * @author Alexander Hinze
 * @since 23/04/2024
 */
@OnlyIn(Dist.CLIENT)
object LauncherRenderer : io.karma.pda.api.client.render.app.AppRenderer<LauncherApp> {
    override fun render(app: LauncherApp, graphics: io.karma.pda.api.client.render.graphics.Graphics) {

    }
}