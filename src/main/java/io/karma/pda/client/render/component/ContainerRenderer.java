/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.component;

import io.karma.pda.api.client.render.component.AbstractComponentRenderer;
import io.karma.pda.api.client.render.component.ComponentRenderers;
import io.karma.pda.api.client.render.gfx.GFX;
import io.karma.pda.api.common.app.component.Component;
import io.karma.pda.api.common.app.component.ComponentType;
import io.karma.pda.api.common.app.component.DefaultContainer;
import io.karma.pda.api.common.flex.FlexNode;
import io.karma.pda.client.flex.ClientFlexNodeHandler;
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
    public void render(final DefaultContainer component, final FlexNode flexNode, final GFX graphics) {
        final var children = component.getChildren();
        for (final var child : children) {
            final var childFlexNode = ClientFlexNodeHandler.INSTANCE.getNode(child);
            if (childFlexNode == null) {
                continue;
            }
            ComponentRenderers.get((ComponentType<Component>) child.getType()).render(child, childFlexNode, graphics);
        }
        super.render(component, flexNode, graphics);
    }
}
