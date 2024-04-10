/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.inventory;

import io.karma.pda.api.common.util.NBTUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public final class ItemStorageView {
    private final ItemStack stack;
    private final String name;

    public ItemStorageView(final ItemStack stack, final String name) {
        this.stack = stack;
        this.name = name;
    }

    private @Nullable CompoundTag getTag() {
        final var tag = stack.getTag();
        if (tag == null || !tag.contains(name)) {
            return null;
        }
        return tag.getCompound(name);
    }

    public ItemStack getStack() {
        return stack;
    }

    public String getName() {
        return name;
    }

    public int getSlotCount() {
        return NBTUtils.getOrDefault(getTag(), ItemStorageContainer.TAG_SLOT_COUNT, 0);
    }

    public int getMaxStackSize() {
        return NBTUtils.getOrDefault(getTag(), ItemStorageContainer.TAG_STACK_SIZE, 0);
    }

    public ItemStack getItem(final int index) {
        final var tag = getTag();
        final var key = ItemStorageContainer.getItemKey(index);
        if (tag == null || !tag.contains(key)) {
            return ItemStack.EMPTY;
        }
        return ItemStack.of(getTag().getCompound(key));
    }
}
