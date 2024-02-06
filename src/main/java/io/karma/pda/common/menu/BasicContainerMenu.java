package io.karma.pda.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 06/02/2024
 */
public class BasicContainerMenu<C extends Container> extends AbstractContainerMenu {
    protected final C container;

    protected BasicContainerMenu(final @Nullable MenuType<?> type, final int id, final C container) {
        super(type, id);
        this.container = container;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(final @NotNull Player player, final int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(final @NotNull Player player) {
        return true;
    }
}
