package io.karma.pda.client.screen;

import io.karma.pda.common.PDAMod;
import io.karma.pda.common.menu.PDAStorageMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 06/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class PDAStorageScreen extends BasicContainerScreen<PDAStorageMenu> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(PDAMod.MODID, "textures/gui/pda_storage.png");

    public PDAStorageScreen(final PDAStorageMenu menu, final Inventory inventory) {
        super(menu, inventory, Component.translatable(String.format("screen.%s.pda_storage", PDAMod.MODID)));
    }

    @Override
    protected void renderBg(final @NotNull GuiGraphics graphics, float partialTicks, final int mouseX,
                            final int mouseY) {
        final var x = (width >> 1) - (imageWidth >> 1);
        final var y = (height >> 1) - (imageHeight >> 1);
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }
}
