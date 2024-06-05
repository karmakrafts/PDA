/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.component;

import io.karma.pda.api.client.render.component.AbstractComponentRenderer;
import io.karma.pda.api.client.render.graphics.Graphics;
import io.karma.pda.api.common.app.component.Spinner;
import io.karma.pda.api.common.flex.FlexNode;
import io.karma.pda.client.render.graphics.GraphicsRenderTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 20/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class SpinnerRenderer extends AbstractComponentRenderer<Spinner> {
    @Override
    public void render(final Spinner component, final FlexNode flexNode, final Graphics graphics) {
        if (component.isVisible()) {
            final var context = graphics.getContext();
            try (final var state = graphics.pushState()) {
                state.setBrush(context.getBrushFactory().create(GraphicsRenderTypes.SPINNER.apply(context.getDisplayMode()),
                    component.color.get(),
                    null));
                state.setForceUVs(true);
                graphics.fillRect(flexNode.getAbsoluteX(),
                    flexNode.getAbsoluteY(),
                    flexNode.getAbsoluteWidth(),
                    flexNode.getAbsoluteHeight());
            }
        }
        super.render(component, flexNode, graphics);
    }
}
