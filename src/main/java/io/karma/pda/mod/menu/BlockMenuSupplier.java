/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * @author Alexander Hinze
 * @since 07/02/2024
 */
@FunctionalInterface
public interface BlockMenuSupplier<M extends AbstractContainerMenu, E extends BlockEntity & Container> {
    M create(final int id, final Inventory inventory, final E entity);
}
