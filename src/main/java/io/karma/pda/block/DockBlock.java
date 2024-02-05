package io.karma.pda.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 05/02/2024
 */
public final class DockBlock extends Block {
    public static final VoxelShape SHAPE = makeShape();

    public DockBlock() {
        super(Properties.of());
    }

    private static VoxelShape makeShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.125, 0, 0.3125, 0.875, 0.125, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0, 0.125, 0.875, 0.25, 0.3125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.125, 0.3125, 0.875, 0.1875, 0.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.125, 0.625, 0.875, 0.1875, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.125, 0.375, 0.1875, 0.1875, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.8125, 0.125, 0.375, 0.875, 0.1875, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.125, 0.375, 0.3125, 0.15625, 0.4375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.125, 0.375, 0.4375, 0.15625, 0.4375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5, 0.125, 0.375, 0.5625, 0.15625, 0.4375), BooleanOp.OR);
        return shape;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isPathfindable(final @NotNull BlockState state, final @NotNull BlockGetter world,
                                  final @NotNull BlockPos pos, final @NotNull PathComputationType type) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getOcclusionShape(final @NotNull BlockState state, final @NotNull BlockGetter world,
                                                 final @NotNull BlockPos pos) {
        return SHAPE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(final @NotNull BlockState state, final @NotNull BlockGetter world,
                                        final @NotNull BlockPos pos, final @NotNull CollisionContext context) {
        return SHAPE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getVisualShape(final @NotNull BlockState state, final @NotNull BlockGetter world,
                                              final @NotNull BlockPos pos, final @NotNull CollisionContext context) {
        return getShape(state, world, pos, context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getCollisionShape(final @NotNull BlockState state, final @NotNull BlockGetter world,
                                                 final @NotNull BlockPos pos, final @NotNull CollisionContext context) {
        return getShape(state, world, pos, context);
    }
}
