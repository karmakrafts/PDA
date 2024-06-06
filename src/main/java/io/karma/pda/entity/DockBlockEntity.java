/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.entity;

import io.karma.pda.block.DockBlock;
import io.karma.pda.init.ModBlockEntities;
import io.karma.pda.inventory.ContainerItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 06/02/2024
 */
public final class DockBlockEntity extends BasicBlockEntity implements Container {
    public static final String TAG_ITEM = "item";
    private final LazyOptional<ContainerItemHandler> itemHandler = LazyOptional.of(() -> new ContainerItemHandler(this));
    private ItemStack stack = ItemStack.EMPTY.copy();

    public DockBlockEntity(final BlockPos pos, final BlockState state) {
        super(ModBlockEntities.dock.get(), pos, state);
    }

    @Override
    protected void readFromNBT(final CompoundTag tag) {
        if (tag.contains(TAG_ITEM)) {
            stack = ItemStack.of(tag.getCompound(TAG_ITEM));
        }
    }

    @Override
    protected void writeToNBT(final CompoundTag tag) {
        tag.put(TAG_ITEM, stack.serializeNBT());
    }

    @Override
    public int getMaxStackSize() {
        return 1;
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
        if (level == null || level.isClientSide) {
            return;
        }
        final var state = level.getBlockState(worldPosition);
        level.setBlockAndUpdate(worldPosition, state.setValue(DockBlock.HAS_ITEM, !stack.isEmpty()));
    }

    @Override
    public boolean stillValid(final @NotNull Player player) {
        return player.distanceToSqr(getBlockPos().getCenter()) <= 64.0;
    }

    @Override
    public void clearContent() {
        stack = ItemStack.EMPTY;
    }

    // Capabilities

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(final @NotNull Capability<T> cap,
                                                      final @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
    }
}
