package io.karma.pda.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 06/02/2024
 */
public abstract class BasicEntityBlock<E extends BlockEntity> extends Block implements EntityBlock {
    protected final Supplier<BlockEntityType<E>> entityTypeSupplier;

    public BasicEntityBlock(final @NotNull Supplier<BlockEntityType<E>> entityTypeSupplier,
                            final @NotNull Properties properties) {
        super(properties);
        this.entityTypeSupplier = entityTypeSupplier;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(final @NotNull BlockPos pos, final @NotNull BlockState state) {
        return entityTypeSupplier.get().create(pos, state);
    }
}
