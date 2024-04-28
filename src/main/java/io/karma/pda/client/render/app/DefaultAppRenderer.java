/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.app;

import io.karma.pda.api.client.render.app.AppRenderer;
import io.karma.pda.api.client.render.component.ComponentRenderers;
import io.karma.pda.api.client.render.graphics.Graphics;
import io.karma.pda.api.common.app.App;
import io.karma.pda.api.common.app.component.ComponentType;
import io.karma.pda.api.common.app.component.Container;
import io.karma.pda.api.common.flex.FlexValue;
import io.karma.pda.api.common.util.Color;
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
    public void render(final A app, final Graphics graphics) {
        final var gfxContext = graphics.getContext();
        final var width = gfxContext.getWidth();
        final var height = gfxContext.getHeight();
        if (!app.isInitialized()) {
            try (final var state = graphics.pushState()) {
                final var brushFactory = graphics.getBrushFactory();
                state.setBrush(brushFactory.createColor(Color.BLACK));
                graphics.fillRect(0, 0, width, height);
                state.setBrush(brushFactory.createColor(Color.WHITE));
                state.setHasTextShadows(false);
                graphics.text(10, 10, "Loading..");
            }
            return;
        }
        final var container = app.getView().getContainer();
        final var flexNode = ClientFlexNodeHandler.INSTANCE.getOrCreateNodeRecursive(container);
        flexNode.setWidth(FlexValue.pixel(width));
        flexNode.setHeight(FlexValue.pixel(height));
        flexNode.computeLayout();
        ComponentRenderers.get((ComponentType<Container>) container.getType()).render(container, flexNode, graphics);
    }

    @Override
    public void dispose(final A app) {
        if (!app.isInitialized()) {
            return;
        }
        ClientFlexNodeHandler.INSTANCE.removeNodeRecursively(app.getView().getContainer());
    }
}
