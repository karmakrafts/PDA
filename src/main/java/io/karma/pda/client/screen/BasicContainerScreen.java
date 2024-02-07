package io.karma.pda.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 07/02/2024
 */
@OnlyIn(Dist.CLIENT)
public abstract class BasicContainerScreen<M extends AbstractContainerMenu> extends AbstractContainerScreen<M> {
    public BasicContainerScreen(final @NotNull M menu, final @NotNull Inventory playerInventory,
                                final @NotNull Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    public void render(final @NotNull GuiGraphics graphics, final int mouseX, final int mouseY,
                       final float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
