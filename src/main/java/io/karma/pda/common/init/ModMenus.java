package io.karma.pda.common.init;

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
    public static RegistryObject<MenuType<PDAStorageMenu>> pdaStorageMenu;

    // @formatter:off
    private ModMenus() {}
    // @formatter:on

    public static void register(final DeferredRegister<MenuType<?>> register) {
        pdaStorageMenu = register.register("pda_item_menu",
            () -> new MenuType<>(PDAStorageMenu::new, FeatureFlagSet.of()));
    }
}
