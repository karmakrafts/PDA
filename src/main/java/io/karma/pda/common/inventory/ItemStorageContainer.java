package io.karma.pda.common.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 06/02/2024
 */
public final class ItemStorageContainer implements Container {
    public static final String TAG_SLOT_COUNT = "slot_count";
    public static final String TAG_STACK_SIZE = "stack_size";

    private final int slotCount;
    private final int maxStackSize;
    private final ItemStack stack;
    private final String name;

    public ItemStorageContainer(final ItemStack stack, final String name, final int slotCount, final int maxStackSize) {
        this.slotCount = slotCount;
        this.maxStackSize = maxStackSize;
        this.stack = stack;
        this.name = name;
    }

    public static String getItemKey(final int index) {
        return String.format("item_%d", index);
    }

    private CompoundTag getOrCreateTag() {
        final var tag = stack.getOrCreateTag();
        if (tag.contains(name)) {
            return tag.getCompound(name);
        }
        final var containerTag = new CompoundTag();
        containerTag.putInt(TAG_SLOT_COUNT, slotCount);
        containerTag.putInt(TAG_STACK_SIZE, maxStackSize);
        final var emptyTag = ItemStack.EMPTY.serializeNBT();
        for (var i = 0; i < slotCount; i++) {
            containerTag.put(getItemKey(i), emptyTag);
        }
        tag.put(name, containerTag);
        return tag;
    }

    @Override
    public int getMaxStackSize() {
        return maxStackSize;
    }

    @Override
    public int getContainerSize() {
        return slotCount;
    }

    @Override
    public boolean isEmpty() {
        for (var i = 0; i < slotCount; i++) {
            if (!getItem(i).isEmpty()) {
                continue;
            }
            return true;
        }
        return false;
    }

    @Override
    public @NotNull ItemStack getItem(final int index) {
        return ItemStack.of(getOrCreateTag().getCompound(getItemKey(index)));
    }

    @Override
    public @NotNull ItemStack removeItem(final int index, int count) {
        if (count <= 0) {
            return ItemStack.EMPTY;
        }
        final var stack = getItem(index);
        final var actualCount = stack.getCount();
        if (count > actualCount) {
            count = actualCount;
        }
        final var result = stack.copy();
        result.setCount(count);
        stack.shrink(count);
        setItem(index, stack);
        return result;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(final int index) {
        return removeItem(index, 1);
    }

    @Override
    public void setItem(final int index, final @NotNull ItemStack stack) {
        getOrCreateTag().put(getItemKey(index), stack.serializeNBT());
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(final @NotNull Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        final var tag = getOrCreateTag();
        final var emptyTag = ItemStack.EMPTY.serializeNBT();
        for (var i = 0; i < slotCount; i++) {
            tag.put(getItemKey(i), emptyTag);
        }
    }
}
