/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.init;

import io.karma.pda.mod.PDAMod;
import io.karma.pda.mod.client.ClientEventHandler;
import io.karma.pda.mod.client.screen.DockStorageScreen;
import io.karma.pda.mod.client.screen.PDAStorageScreen;
import io.karma.pda.mod.menu.DockStorageMenu;
import io.karma.pda.mod.menu.PDAStorageMenu;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * @author Alexander Hinze
 * @since 21/08/2024
 */
public final class ModScreens {
    // @formatter:off
    private ModScreens() {}
    // @formatter:on

    public static void register() {
        PDAMod.LOGGER.info("Registering screens");
        MenuScreens.register(ModMenus.pdaStorage.get(),
            (PDAStorageMenu menu, Inventory inventory, Component title) -> new PDAStorageScreen(menu, inventory));
        MenuScreens.register(ModMenus.dockStorage.get(),
            (DockStorageMenu menu, Inventory inventory, Component title) -> new DockStorageScreen(menu, inventory));
        ClientEventHandler.INSTANCE.fireRegisterEvents();
    }
}
