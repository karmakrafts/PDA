/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 17/02/2024
 */
public final class ContainerItemHandler implements IItemHandler {
    private final Container container;

    public ContainerItemHandler(final Container container) {
        this.container = container;
    }

    @Override
    public int getSlots() {
        return container.getContainerSize();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(final int index) {
        return container.getItem(index);
    }

    @Override
    public @NotNull ItemStack insertItem(final int index, final @NotNull ItemStack stack, final boolean simulate) {
        return ItemStack.EMPTY; // TODO: implement this
    }

    @Override
    public @NotNull ItemStack extractItem(final int index, final int count, final boolean simulate) {
        return ItemStack.EMPTY; // TODO: implement this
    }

    @Override
    public int getSlotLimit(final int index) {
        return container.getMaxStackSize();
    }

    @Override
    public boolean isItemValid(final int index, final @NotNull ItemStack stack) {
        return container.canPlaceItem(index, stack);
    }
}
