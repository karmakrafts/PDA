/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.component;

import io.karma.pda.api.client.render.component.AbstractComponentRenderer;
import io.karma.pda.api.client.render.graphics.Graphics;
import io.karma.pda.api.common.app.component.Label;
import io.karma.pda.api.common.flex.FlexNode;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class LabelRenderer extends AbstractComponentRenderer<Label> {
    @Override
    public void render(final Label component, final FlexNode flexNode, final Graphics graphics) {
        try (final var state = graphics.pushState()) {
            state.setFont(component.font.get());
            state.setBrush(graphics.getContext().getBrushFactory().create(component.color.get()));
            graphics.text(flexNode.getAbsoluteX(),
                flexNode.getAbsoluteY(),
                flexNode.getAbsoluteWidth(),
                component.text.get());
        }
        super.render(component, flexNode, graphics);
    }
}
