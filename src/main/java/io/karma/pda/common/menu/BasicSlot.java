package io.karma.pda.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 07/02/2024
 */
public class BasicSlot extends Slot {
    protected final ItemStack icon;

    public BasicSlot(final @NotNull Container container, final int id, final int x, final int y, final ItemStack icon) {
        super(container, id, x, y);
        this.icon = icon;
    }

    public BasicSlot(final @NotNull Container container, final int id, final int x, final int y) {
        this(container, id, x, y, ItemStack.EMPTY);
    }

    public ItemStack getIcon() {
        return icon;
    }
}
