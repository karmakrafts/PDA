/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 06/02/2024
 */
public class BasicContainerMenu<C extends Container> extends AbstractContainerMenu {
    protected final C container;

    protected BasicContainerMenu(final @Nullable MenuType<?> type, final int id, final C container) {
        super(type, id);
        this.container = container;
    }

    @Override
    public boolean stillValid(final @NotNull Player player) {
        return true;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(final @NotNull Player player, final int index) {
        return ItemStack.EMPTY;
    }

    // Ported from CCLs ContainerExtended
    // https://github.com/TheCBProject/CodeChickenLib/blob/master/src/main/java/codechicken/lib/inventory/container/ContainerExtended.java#L167-L176
    protected void bindPlayerInventory(final Inventory inventoryPlayer, final int x, final int y) {
        for (var row = 0; row < 3; row++) {
            for (var col = 0; col < 9; col++) {
                addSlot(new BasicSlot(inventoryPlayer, col + row * 9 + 9, x + col * 18, y + row * 18));
            }
        }
        for (var slot = 0; slot < 9; slot++) {
            addSlot(new BasicSlot(inventoryPlayer, slot, x + slot * 18, y + 58));
        }
    }
}
