package io.karma.pda.client.screen;

import io.karma.pda.common.menu.PDAItemMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 06/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class PDAItemScreen extends AbstractContainerScreen<PDAItemMenu> {
    public PDAItemScreen(final PDAItemMenu menu, final Inventory inventory, final Component component) {
        super(menu, inventory, component);
    }

    @Override
    protected void renderBg(final @NotNull GuiGraphics graphics, float partialTicks, final int mouseX,
                            final int mouseY) {

    }
}
