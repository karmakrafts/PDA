package io.karma.pda.common.init;

import io.karma.pda.common.entity.DockBlockEntity;
import io.karma.pda.common.menu.BlockMenuFactory;
import io.karma.pda.common.menu.DockMenu;
import io.karma.pda.common.menu.ItemMenuFactory;
import io.karma.pda.common.menu.PDAStorageMenu;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

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

    public static void register(final DeferredRegister<MenuType<?>> register) {
        pdaStorage = register.register("pda_storage",
            () -> new MenuType<>(new ItemMenuFactory<>(PDAStorageMenu::new), FeatureFlagSet.of()));
        dock = register.register("dock",
            () -> new MenuType<>(new BlockMenuFactory<>(DockMenu::new, DockBlockEntity.class), FeatureFlagSet.of()));
    }
}
