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
import io.karma.pda.foundation.component.DefaultContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ContainerRenderer extends AbstractComponentRenderer<DefaultContainer> {
    @SuppressWarnings("unchecked")
    @Override
    public void render(final DefaultContainer component, final FlexNode flexNode, final Graphics graphics) {
        if (component.isVisible()) {
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
