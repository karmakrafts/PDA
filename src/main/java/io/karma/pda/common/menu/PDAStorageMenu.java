package io.karma.pda.common.menu;

import io.karma.pda.common.init.ModItems;
import io.karma.pda.common.init.ModMenus;
import io.karma.pda.common.inventory.ItemStorageContainer;
import io.karma.pda.common.item.PDAItem;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 06/02/2024
 */
public final class PDAStorageMenu extends BasicContainerMenu<ItemStorageContainer> {
    public PDAStorageMenu(final int id, final Inventory playerInventory, final ItemStack stack) {
        // @formatter:off
        super(ModMenus.pdaStorage.get(),
            id,
            new ItemStorageContainer(stack, "pda_storage", 1, 1));
        // @formatter:on
        bindPlayerInventory(playerInventory, 8, 84);
        addSlot(new FilteredSlot(container, 0, 80, 35, ModItems.memoryCard.get()).withSetCallback(newStack -> {
            final var tag = stack.getOrCreateTag();
            if (tag.contains(PDAItem.TAG_IS_ON)) {
                tag.remove(PDAItem.TAG_IS_ON);
            }
            else {
                tag.putBoolean(PDAItem.TAG_IS_ON, true);
            }
        }));
    }

    @Override
    public boolean stillValid(final @NotNull Player player) {
        return container.stillValid(player);
    }
}
