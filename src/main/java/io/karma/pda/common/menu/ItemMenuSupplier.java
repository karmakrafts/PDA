/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

/**
 * @author Alexander Hinze
 * @since 07/02/2024
 */
@FunctionalInterface
public interface ItemMenuSupplier<M extends AbstractContainerMenu> {
    M create(final int id, final Inventory inventory, final ItemStack stack);
}
