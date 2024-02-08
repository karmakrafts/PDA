package io.karma.pda.common.entity;

import io.karma.pda.common.block.DockBlock;
import io.karma.pda.common.init.ModBlockEntities;
import io.karma.pda.common.inventory.ItemStorageView;
import io.karma.pda.common.menu.PDAStorageMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
    }

    @Override
    public boolean stillValid(final @NotNull Player player) {
        return player.distanceToSqr(getBlockPos().getCenter()) <= 64.0;
    }

    @Override
    public void clearContent() {
        stack = ItemStack.EMPTY;
    }

    public void updateBlockState(final Level world, final BlockPos pos) {
        if (world.isClientSide) {
            return;
        }
        final var blockState = world.getBlockState(pos);
        if (stack.isEmpty()) {
            world.setBlock(pos, blockState.setValue(DockBlock.STATE, DockBlock.State.NO_ITEM), 3);
            return;
        }
        final var itemStorageView = new ItemStorageView(stack, PDAStorageMenu.CONTAINER_NAME);
        final var memoryCardStack = itemStorageView.getItem(0);
        final var state = memoryCardStack.isEmpty() ? DockBlock.State.ITEM_OFF : DockBlock.State.ITEM_ON;
        world.setBlock(pos, blockState.setValue(DockBlock.STATE, state), 3);
    }
}
