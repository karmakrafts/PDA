package io.karma.pda.entity;

import io.karma.pda.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 06/02/2024
 */
public final class DockBlockEntity extends BasicBlockEntity {
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

    public @NotNull ItemStack getStack() {
        return stack;
    }

    public void setStack(final @NotNull ItemStack stack) {
        this.stack = stack;
    }

    public static final class DockBlockEntityFactory implements BlockEntityType.BlockEntitySupplier<DockBlockEntity> {
        @Override
        public @NotNull DockBlockEntity create(final @NotNull BlockPos pos, final @NotNull BlockState state) {
            return new DockBlockEntity(pos, state);
        }
    }
}
