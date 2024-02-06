package io.karma.pda.common.entity;

import io.karma.pda.common.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 06/02/2024
 */
public final class DockBlockEntity extends BasicBlockEntity implements Container {
    public static final String TAG_ITEM = "item";
    private ItemStack stack = ItemStack.EMPTY.copy();

    public DockBlockEntity(final BlockPos pos, final BlockState state) {
        super(ModBlockEntities.dock.get(), pos, state);
    }

    @Override
    public void deserializeNBT(final @NotNull CompoundTag tag) {
        if (tag.contains(TAG_ITEM)) {
            stack.deserializeNBT(tag.getCompound(TAG_ITEM));
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        final var tag = super.serializeNBT();
        tag.put(TAG_ITEM, stack.serializeNBT());
        return tag;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(final int index) {
        if (index != 0) {
            return ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    public @NotNull ItemStack removeItem(final int index, final int count) {
        if (index != 0 || count <= 0) {
            return ItemStack.EMPTY;
        }
        final var result = stack;
        stack = ItemStack.EMPTY;
        return result;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(final int index) {
        if (index != 0) {
            return ItemStack.EMPTY;
        }
        final var result = stack;
        stack = ItemStack.EMPTY;
        return result;
    }

    @Override
    public void setItem(final int index, final @NotNull ItemStack stack) {
        if (index != 0) {
            return;
        }
        this.stack = stack;
    }

    @Override
    public boolean stillValid(final @NotNull Player player) {
        return player.distanceToSqr(getBlockPos().getCenter()) <= 64.0;
    }

    @Override
    public void clearContent() {
        stack = ItemStack.EMPTY;
    }

    public static final class DockBlockEntityFactory implements BlockEntityType.BlockEntitySupplier<DockBlockEntity> {
        @Override
        public @NotNull DockBlockEntity create(final @NotNull BlockPos pos, final @NotNull BlockState state) {
            return new DockBlockEntity(pos, state);
        }
    }
}
