/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.app;

import io.karma.pda.api.app.App;
import io.karma.pda.api.app.component.ComponentType;
import io.karma.pda.api.app.component.Container;
import io.karma.pda.api.app.theme.font.DefaultFontFamilies;
import io.karma.pda.api.app.theme.font.FontStyle;
import io.karma.pda.api.client.render.app.AppRenderer;
import io.karma.pda.api.client.render.component.ComponentRenderers;
import io.karma.pda.api.client.render.graphics.Graphics;
import io.karma.pda.api.color.Color;
import io.karma.pda.api.flex.FlexValue;
import io.karma.pda.mod.client.flex.ClientFlexNodeHandler;
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
        final var context = graphics.getContext();
        final var width = context.getWidth();
        final var height = context.getHeight();
        if (!app.isInitialized()) {
            try (final var state = graphics.pushState()) {
                final var brushFactory = context.getBrushFactory();
                state.setFont(DefaultFontFamilies.FIXEDSYS.getFont(FontStyle.REGULAR, 16F));
                state.setBrush(brushFactory.create(Color.BLACK));
                graphics.fillRect(0, 0, width, height);
                state.setBrush(brushFactory.create(Color.WHITE));
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
