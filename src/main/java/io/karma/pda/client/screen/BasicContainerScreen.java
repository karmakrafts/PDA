package io.karma.pda.client.screen;

import io.karma.pda.common.menu.BasicSlot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
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

    // AT
    @Override
    public void renderSlot(final @NotNull GuiGraphics graphics, final @NotNull Slot slot) {
        super.renderSlot(graphics, slot);
        if (slot instanceof BasicSlot basicSlot) {
            final var icon = basicSlot.getIcon();
            if (icon.isEmpty()) {
                return;
            }
            graphics.setColor(1F, 1F, 1F, 0.2F);
            graphics.renderItem(icon, slot.x, slot.y);
            graphics.setColor(1F, 1F, 1F, 1F);
        }
    }

    @Override
    public void render(final @NotNull GuiGraphics graphics, final int mouseX, final int mouseY,
                       final float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
