package io.karma.pda.common.menu;

import io.karma.pda.common.init.ModMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 06/02/2024
 */
public final class PDAItemMenu extends AbstractContainerMenu {
    private final Inventory playerInventory;

    public PDAItemMenu(final int id, final Inventory playerInventory) {
        super(ModMenus.pdaItemMenu.get(), id);
        this.playerInventory = playerInventory;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(final @NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(final @NotNull Player player) {
        return true;
    }
}
