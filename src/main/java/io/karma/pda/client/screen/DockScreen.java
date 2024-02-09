package io.karma.pda.client.screen;

import io.karma.pda.client.util.ScreenUtils;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.menu.DockMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 07/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DockScreen extends BasicContainerScreen<DockMenu> {
    public DockScreen(@NotNull DockMenu menu, @NotNull Inventory playerInventory) {
        super(menu, playerInventory, Component.translatable(String.format("screen.%s.dock", PDAMod.MODID)));
    }

    @Override
    protected void renderBg(final @NotNull GuiGraphics graphics, final float partialTick, final int mouseX,
                            final int mouseY) {
        final var x = (width >> 1) - (imageWidth >> 1);
        final var y = (height >> 1) - (imageHeight >> 1);
        ScreenUtils.drawBackground(graphics, x, y, imageWidth, imageHeight);
    }
}
