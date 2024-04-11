/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.component;

import com.mojang.blaze3d.vertex.PoseStack;
import io.karma.pda.api.client.render.ComponentRenderer;
import io.karma.pda.api.client.render.ComponentRenderers;
import io.karma.pda.api.common.app.component.Component;
import io.karma.pda.api.common.app.component.ComponentType;
import io.karma.pda.api.common.app.component.DefaultContainer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ContainerRenderer implements ComponentRenderer<DefaultContainer> {
    @SuppressWarnings("unchecked")
    @Override
    public void render(final DefaultContainer component, final MultiBufferSource bufferSource,
                       final PoseStack poseStack) {
        final var children = component.getChildren();
        for (final var child : children) {
            final var renderer = ComponentRenderers.get((ComponentType<Component>) child.getType());
            renderer.render(child, bufferSource, poseStack);
        }
    }
}
