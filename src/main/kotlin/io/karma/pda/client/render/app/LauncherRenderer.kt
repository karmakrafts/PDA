/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.app

import io.karma.pda.api.client.render.app.AppRenderer
import io.karma.pda.api.client.render.graphics.Graphics
import io.karma.pda.common.app.LauncherApp
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

/**
 * @author Alexander Hinze
 * @since 23/04/2024
 */
@OnlyIn(Dist.CLIENT)
object LauncherRenderer : AppRenderer<LauncherApp> {
    override fun render(app: LauncherApp, graphics: Graphics) {

    }
}