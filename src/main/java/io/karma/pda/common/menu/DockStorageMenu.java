/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.menu;

import io.karma.pda.common.entity.DockBlockEntity;
import io.karma.pda.common.init.ModItems;
import io.karma.pda.common.init.ModMenus;
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
