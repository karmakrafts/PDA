/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.component;

import io.karma.pda.api.client.render.graphics.Graphics;
import io.karma.pda.api.app.component.Component;
import io.karma.pda.api.flex.FlexNode;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 21/04/2024
 */
@OnlyIn(Dist.CLIENT)
public abstract class AbstractComponentRenderer<C extends Component> implements ComponentRenderer<C> {
    @Override
    public void render(final C component, final FlexNode flexNode, final Graphics graphics) {
        final var context = graphics.getContext();
        if (context.isDebugMode()) {
            try (final var state = graphics.pushState()) {
                state.setBrush(context.getBrushFactory().createDebug(component));
                graphics.drawRect(flexNode.getAbsoluteX(),
                    flexNode.getAbsoluteY(),
                    flexNode.getAbsoluteWidth(),
                    flexNode.getAbsoluteHeight());
            }
        }
    }
}
