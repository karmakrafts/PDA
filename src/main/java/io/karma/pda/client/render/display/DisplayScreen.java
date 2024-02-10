package io.karma.pda.client.render.display;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 09/02/2024
 */
@OnlyIn(Dist.CLIENT)
public interface DisplayScreen {
    void render(final PoseStack poseStack, final int mouseX, final int mouseY);
}
