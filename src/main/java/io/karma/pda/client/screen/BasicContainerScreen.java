/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.screen;

import io.karma.pda.client.util.ScreenUtils;
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

    // AT: Allow rendering of ghost items using a subtype of the regular Slot class
    @Override
    public void renderSlot(final @NotNull GuiGraphics graphics, final @NotNull Slot slot) {
        super.renderSlot(graphics, slot);
        if (slot instanceof BasicSlot basicSlot) {
            ScreenUtils.drawSlot(graphics, basicSlot.x - 1, basicSlot.y - 1);
            final var icon = basicSlot.getIcon();
            if (icon.isEmpty()) {
                return;
            }
            graphics.setColor(1F, 1F, 1F, 0.33F);
            graphics.renderItem(icon, slot.x, slot.y);
            graphics.setColor(1F, 1F, 1F, 1F);
        }
    }

    // AT: Allow changing the color of the default container labels
    @Override
    public void renderLabels(final @NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        final var color = getLabelColor();
        graphics.drawString(font, title, titleLabelX, titleLabelY, color, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, color, false);
    }

    @Override
    public void render(final @NotNull GuiGraphics graphics, final int mouseX, final int mouseY,
                       final float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    public int getLabelColor() {
        return 0xFF101010;
    }
}
