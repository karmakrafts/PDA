/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.app;

import com.mojang.blaze3d.vertex.PoseStack;
import io.karma.pda.api.client.render.AppRenderer;
import io.karma.pda.api.client.render.ComponentRenderers;
import io.karma.pda.api.common.app.App;
import io.karma.pda.api.common.app.component.ComponentType;
import io.karma.pda.api.common.app.component.Container;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultAppRenderer<A extends App> implements AppRenderer<A> {
    @SuppressWarnings("unchecked")
    @Override
    public void render(final A app, final MultiBufferSource bufferSource, final PoseStack poseStack) {
        final var container = app.getContainer();
        final var renderer = ComponentRenderers.get((ComponentType<Container>) container.getType());
        renderer.render(container, bufferSource, poseStack);
    }
}
