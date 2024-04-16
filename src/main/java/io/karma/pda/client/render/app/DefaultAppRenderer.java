/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.app;

import io.karma.pda.api.client.render.app.AppRenderer;
import io.karma.pda.api.client.render.component.ComponentRenderers;
import io.karma.pda.api.client.render.gfx.GFX;
import io.karma.pda.api.common.app.App;
import io.karma.pda.api.common.app.component.ComponentType;
import io.karma.pda.api.common.app.component.Container;
import io.karma.pda.client.flex.ClientFlexNodeHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultAppRenderer<A extends App> implements AppRenderer<A> {
    @SuppressWarnings("unchecked")
    @Override
    public void render(final A app, final GFX graphics) {
        final var container = app.getView().getContainer();
        final var flexNode = ClientFlexNodeHandler.INSTANCE.getOrCreateNode(container);
        ComponentRenderers.get((ComponentType<Container>) container.getType()).render(container, flexNode, graphics);
    }

    @Override
    public void cleanup(final A app, final GFX graphics) {
        ClientFlexNodeHandler.INSTANCE.removeNode(app.getView().getContainer());
    }
}
