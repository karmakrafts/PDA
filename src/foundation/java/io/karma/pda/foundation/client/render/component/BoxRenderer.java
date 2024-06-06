/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.foundation.client.render.component;

import io.karma.pda.api.app.component.Component;
import io.karma.pda.api.app.component.ComponentType;
import io.karma.pda.api.client.ClientAPI;
import io.karma.pda.api.client.render.component.AbstractComponentRenderer;
import io.karma.pda.api.client.render.component.ComponentRenderers;
import io.karma.pda.api.client.render.graphics.Graphics;
import io.karma.pda.api.flex.FlexNode;
import io.karma.pda.foundation.component.Box;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 23/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class BoxRenderer extends AbstractComponentRenderer<Box> {
    @SuppressWarnings("unchecked")
    @Override
    public void render(final Box component, final FlexNode flexNode, final Graphics graphics) {
        if (component.isVisible()) {
            try (final var state = graphics.pushState()) {
                final var x = flexNode.getAbsoluteX();
                final var y = flexNode.getAbsoluteY();
                final var w = flexNode.getAbsoluteWidth();
                final var h = flexNode.getAbsoluteHeight();
                final var brushFactory = graphics.getContext().getBrushFactory();
                state.setBrush(brushFactory.create(component.background.get()));
                graphics.fillRect(x, y, w, h);
                state.setBrush(brushFactory.create(component.foreground.get()));
                graphics.drawRect(x, y, w, h);
            }
            for (final var child : component.getChildren()) {
                final var childFlexNode = ClientAPI.getFlexNodeHandler().getNode(child);
                if (childFlexNode == null) {
                    continue;
                }
                ComponentRenderers.get((ComponentType<Component>) child.getType()).render(child,
                    childFlexNode,
                    graphics.copyWithContext(graphics.getContext().derive(childFlexNode.getAbsoluteWidth(),
                        childFlexNode.getAbsoluteHeight())));
            }
        }
        super.render(component, flexNode, graphics);
    }
}
