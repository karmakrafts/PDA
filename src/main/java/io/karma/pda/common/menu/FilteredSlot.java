/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * @author Alexander Hinze
 * @since 07/02/2024
 */
public final class FilteredSlot extends BasicSlot {
    private final Predicate<ItemStack> filter;

    public FilteredSlot(final @NotNull Container container, final int id, final int x, final int y,
                        final Predicate<ItemStack> filter, final ItemStack icon) {
        super(container, id, x, y, icon);
        this.filter = filter;
    }

    public FilteredSlot(final @NotNull Container container, final int id, final int x, final int y,
                        final Predicate<ItemStack> filter) {
        this(container, id, x, y, filter, ItemStack.EMPTY);
    }

    public FilteredSlot(final @NotNull Container container, final int id, final int x, final int y, final Item item) {
        this(container, id, x, y, stack -> stack.getItem() == item, new ItemStack(item, 1));
    }

    public FilteredSlot(final @NotNull Container container, final int id, final int x, final int y, final Block block) {
        this(container, id, x, y, stack -> Block.byItem(stack.getItem()) == block, new ItemStack(block, 1));
    }

    @Override
    public boolean mayPlace(final @NotNull ItemStack stack) {
        return filter.test(stack);
    }
}
