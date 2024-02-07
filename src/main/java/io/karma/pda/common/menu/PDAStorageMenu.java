package io.karma.pda.common.menu;

import io.karma.pda.common.init.ModMenus;
import io.karma.pda.common.inventory.ItemStorageContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 06/02/2024
 */
public final class PDAStorageMenu extends BasicContainerMenu<ItemStorageContainer> {
    public PDAStorageMenu(final int id, final Inventory playerInventory, final ItemStack stack) {
        // @formatter:off
        super(ModMenus.pdaStorageMenu.get(),
            id,
            new ItemStorageContainer(stack, "pda_storage", 1, 1));
        // @formatter:on
        bindPlayerInventory(playerInventory, 8, 84);
        addSlot(new Slot(container, 0, 80, 35));
    }

    @Override
    public boolean stillValid(final @NotNull Player player) {
        return container.stillValid(player);
    }
}
