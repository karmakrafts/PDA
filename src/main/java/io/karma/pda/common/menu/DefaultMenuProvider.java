package io.karma.pda.common.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 07/02/2024
 */
public final class DefaultMenuProvider<M extends AbstractContainerMenu> implements MenuProvider {
    private final Supplier<MenuType<M>> typeProvider;

    public DefaultMenuProvider(final Supplier<MenuType<M>> typeProvider) {
        this.typeProvider = typeProvider;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.empty();
    }

    @Override
    public AbstractContainerMenu createMenu(final int id, final @NotNull Inventory inventory,
                                            final @NotNull Player player) {
        return typeProvider.get().create(id, inventory);
    }
}
