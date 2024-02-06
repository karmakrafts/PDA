package io.karma.pda.common.menu;

import io.karma.pda.common.init.ModMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 06/02/2024
 */
public final class PDAItemMenu extends AbstractContainerMenu {
    public PDAItemMenu(final int id) {
        super(ModMenus.pdaItemMenu.get(), id);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(final @NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(final @NotNull Player player) {
        return true;
    }

    public static final class PDAItemMenuFactory implements MenuType.MenuSupplier<PDAItemMenu> {
        @Override
        public @NotNull PDAItemMenu create(final int id, final @NotNull Inventory inventory) {
            return new PDAItemMenu(id);
        }
    }
}
