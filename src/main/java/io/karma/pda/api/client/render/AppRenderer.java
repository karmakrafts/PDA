package io.karma.pda.api.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.karma.pda.api.common.app.App;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
@OnlyIn(Dist.CLIENT)
public interface AppRenderer<A extends App> {
    void render(final A app, final MultiBufferSource bufferSource, final PoseStack poseStack);
}
