package io.karma.pda.common.menu;

import io.karma.pda.common.init.ModMenus;
import io.karma.pda.common.util.ItemStorageContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 06/02/2024
 */
public final class PDAStorageMenu extends BasicContainerMenu<ItemStorageContainer> {
    private final Inventory playerInventory;

    public PDAStorageMenu(final int id, final Inventory playerInventory) {
        super(ModMenus.pdaStorageMenu.get(),
            id,
            new ItemStorageContainer(playerInventory.getItem(playerInventory.selected), "pda_storage", 1, 1));
        this.playerInventory = playerInventory;
        bindPlayerInventory(playerInventory, 8, 12);
        addSlot(new Slot(container, 0, 8, 0));
    }

    @Override
    public boolean stillValid(final @NotNull Player player) {
        return container.stillValid(player);
    }
}
