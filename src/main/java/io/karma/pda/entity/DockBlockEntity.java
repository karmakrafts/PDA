package io.karma.pda.entity;

import io.karma.pda.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 06/02/2024
 */
public final class DockBlockEntity extends BasicBlockEntity {
    public DockBlockEntity(final BlockPos pos, final BlockState state) {
        super(ModBlockEntities.dock.get(), pos, state);
    }

    @Override
    public void deserializeNBT(final @NotNull CompoundTag tag) {

    }

    @Override
    public CompoundTag serializeNBT() {
        final var tag = super.serializeNBT();
        return tag;
    }

    public static final class DockBlockEntityFactory implements BlockEntityType.BlockEntitySupplier<DockBlockEntity> {
        @Override
        public @NotNull DockBlockEntity create(final @NotNull BlockPos pos, final @NotNull BlockState state) {
            return new DockBlockEntity(pos, state);
        }
    }
}
