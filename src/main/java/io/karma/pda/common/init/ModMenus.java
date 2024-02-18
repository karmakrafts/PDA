/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.init;

import io.karma.pda.common.PDAMod;
import io.karma.pda.common.entity.DockBlockEntity;
import io.karma.pda.common.menu.BlockMenuFactory;
import io.karma.pda.common.menu.DockMenu;
import io.karma.pda.common.menu.ItemMenuFactory;
import io.karma.pda.common.menu.PDAStorageMenu;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Alexander Hinze
 * @since 06/02/2024
 */
public final class ModMenus {
    public static RegistryObject<MenuType<PDAStorageMenu>> pdaStorage;
    public static RegistryObject<MenuType<DockMenu>> dock;

    // @formatter:off
    private ModMenus() {}
    // @formatter:on

    @ApiStatus.Internal
    public static void register(final DeferredRegister<MenuType<?>> register) {
        PDAMod.LOGGER.info("Registering menus");
        pdaStorage = register.register("pda_storage",
            () -> new MenuType<>(new ItemMenuFactory<>(PDAStorageMenu::new), FeatureFlagSet.of()));
        dock = register.register("dock",
            () -> new MenuType<>(new BlockMenuFactory<>(DockMenu::new, DockBlockEntity.class), FeatureFlagSet.of()));
    }
}
