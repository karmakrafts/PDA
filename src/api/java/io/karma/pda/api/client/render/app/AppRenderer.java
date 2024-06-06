/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.app;

import io.karma.pda.api.app.App;
import io.karma.pda.api.client.render.graphics.Graphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
@OnlyIn(Dist.CLIENT)
public interface AppRenderer<A extends App> {
    void render(final A app, final Graphics graphics);

    default void dispose(final A app) {
    }
}
