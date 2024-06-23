/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.menu;

import io.karma.pda.mod.init.ModItems;
import io.karma.pda.mod.init.ModMenus;
import io.karma.pda.mod.inventory.ItemStorageContainer;
import io.karma.pda.mod.item.PDAItem;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 06/02/2024
 */
public final class PDAStorageMenu extends BasicContainerMenu<ItemStorageContainer> {
    public static final String CONTAINER_NAME = "pda_storage";

    public PDAStorageMenu(final int id, final Inventory playerInventory, final ItemStack stack) {
        // @formatter:off
        super(ModMenus.pdaStorage.get(),
            id,
            new ItemStorageContainer(stack, CONTAINER_NAME, 1, 1));
        // @formatter:on
        bindPlayerInventory(playerInventory, 8, 84);
        addSlot(new FilteredSlot(container, 0, 80, 35, ModItems.memoryCard.get()).withSetCallback(newStack -> {
            final var tag = stack.getOrCreateTag();
            if (tag.contains(PDAItem.TAG_HAS_CARD)) {
                tag.remove(PDAItem.TAG_HAS_CARD);
            }
            else {
                tag.putBoolean(PDAItem.TAG_HAS_CARD, true);
            }
        }));
    }

    @Override
    public boolean stillValid(final @NotNull Player player) {
        return container.stillValid(player);
    }
}
