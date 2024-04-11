/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.component;

import com.mojang.blaze3d.vertex.PoseStack;
import io.karma.pda.api.client.render.ComponentRenderer;
import io.karma.pda.api.common.app.component.Label;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class LabelRenderer implements ComponentRenderer<Label> {
    @Override
    public void render(final Label component, final MultiBufferSource bufferSource, final PoseStack poseStack) {

    }
}
