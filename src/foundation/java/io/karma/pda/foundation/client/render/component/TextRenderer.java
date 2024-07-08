/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.foundation.client.render.component;

import io.karma.pda.api.client.render.component.AbstractComponentRenderer;
import io.karma.pda.api.client.render.graphics.Graphics;
import io.karma.pda.api.flex.FlexNode;
import io.karma.pda.foundation.component.Text;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class TextRenderer extends AbstractComponentRenderer<Text> {
    @Override
    public void render(final Text component, final FlexNode flexNode, final Graphics graphics) {
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
