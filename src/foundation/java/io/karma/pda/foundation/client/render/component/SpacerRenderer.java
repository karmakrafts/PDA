/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.foundation.client.render.component;

import io.karma.pda.api.client.render.component.AbstractComponentRenderer;
import io.karma.pda.api.client.render.graphics.Graphics;
import io.karma.pda.api.flex.FlexNode;
import io.karma.pda.foundation.component.Spacer;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public final class SpacerRenderer extends AbstractComponentRenderer<Spacer> {
    @Override
    public void render(final Spacer component, final FlexNode flexNode, final Graphics graphics) {
        if (component.isVisible()) {
            try (final var state = graphics.pushState()) {
                int x;
                int y;
                int rw;
                int rh;
                final var width = flexNode.getAbsoluteWidth();
                final var height = flexNode.getAbsoluteHeight();
                final var sepWidth = component.width.get();
                if (component.orientation.get() == Spacer.Orientation.HORIZONTAL) {
                    x = flexNode.getAbsoluteX();
                    y = flexNode.getAbsoluteY() + (height >> 1) - (sepWidth >> 1);
                    rw = width;
                    rh = sepWidth;
                }
                else {
                    x = flexNode.getAbsoluteX() + (width >> 1) - (sepWidth >> 1);
                    y = flexNode.getAbsoluteY();
                    rw = sepWidth;
                    rh = height;
                }
                state.setBrush(graphics.getContext().getBrushFactory().create(component.color.get()));
                graphics.fillRect(x, y, rw, rh);
            }
        }
        super.render(component, flexNode, graphics);
    }
}
