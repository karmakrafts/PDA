package io.karma.pda.client.render.display;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
@OnlyIn(Dist.CLIENT)
public interface DisplayRenderContext {
    PoseStack getPoseStack();

    Font getDefaultFont();

    float getPartialTick();

    int getMouseX();

    int getMouseY();
}
