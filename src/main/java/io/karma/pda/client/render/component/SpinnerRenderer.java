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
        try (final var state = graphics.pushState()) {
            state.setBrush(graphics.getBrushFactory().create(GraphicsRenderTypes.SPINNER, component.color.get(), null));
            state.setForceUVs(true);
            final var width = flexNode.getAbsoluteWidth();
            final var height = flexNode.getAbsoluteHeight();
            graphics.fillRect(flexNode.getAbsoluteX(), flexNode.getAbsoluteY(), width, height);
        }
        super.render(component, flexNode, graphics);
    }
}
