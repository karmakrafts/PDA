/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.menu;

import io.karma.pda.entity.DockBlockEntity;
import io.karma.pda.init.ModItems;
import io.karma.pda.init.ModMenus;
import net.minecraft.world.entity.player.Inventory;

/**
 * @author Alexander Hinze
 * @since 07/02/2024
 */
public final class DockStorageMenu extends BasicContainerMenu<DockBlockEntity> {
    public DockStorageMenu(final int id, final Inventory playerInventory, final DockBlockEntity container) {
        super(ModMenus.dockStorage.get(), id, container);
        bindPlayerInventory(playerInventory, 8, 84);
        addSlot(new FilteredSlot(container, 0, 80, 35, ModItems.pda.get()));
    }
}
