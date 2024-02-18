package io.karma.pda.api.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.karma.pda.api.common.app.component.Component;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 17/02/2024
 */
@OnlyIn(Dist.CLIENT)
public interface ComponentRenderer<C extends Component> {
    void render(final C component, final MultiBufferSource bufferSource, final PoseStack poseStack);
}
