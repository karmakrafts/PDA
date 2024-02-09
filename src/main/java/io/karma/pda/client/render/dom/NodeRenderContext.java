package io.karma.pda.client.render.dom;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
@OnlyIn(Dist.CLIENT)
public interface NodeRenderContext {
    GuiGraphics getGraphics();

    Font getDefaultFont();

    float getPartialTick();

    int getMouseX();

    int getMouseY();
}
