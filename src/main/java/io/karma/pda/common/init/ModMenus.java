package io.karma.pda.common.init;

import io.karma.pda.common.menu.PDAItemMenu;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * @author Alexander Hinze
 * @since 06/02/2024
 */
public final class ModMenus {
    public static RegistryObject<MenuType<PDAItemMenu>> pdaItemMenu;

    // @formatter:off
    private ModMenus() {}
    // @formatter:on

    public static void register(final DeferredRegister<MenuType<?>> register) {
        pdaItemMenu = register.register("pda_item_menu", () -> new MenuType<>(PDAItemMenu::new, FeatureFlagSet.of()));
    }
}
