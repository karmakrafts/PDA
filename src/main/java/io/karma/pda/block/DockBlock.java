package io.karma.pda.block;

import codechicken.lib.vec.Vector3;
import io.karma.pda.entity.DockBlockEntity;
import io.karma.pda.init.ModBlockEntities;
import io.karma.pda.util.ShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;

/**
 * @author Alexander Hinze
 * @since 05/02/2024
 */
public final class DockBlock extends BasicEntityBlock<DockBlockEntity> {
    public static final EnumProperty<Direction> ORIENTATION = EnumProperty.create("orientation", Direction.class);
    public static final BooleanProperty HAS_ITEM = BooleanProperty.create("has_item");
    public static final BooleanProperty IS_ON = BooleanProperty.create("is_on");
    public static final EnumMap<Direction, VoxelShape> SHAPE = new EnumMap<>(Direction.class);

    static {
        for (final var dir : Direction.values()) {
            if (!dir.getAxis().isHorizontal()) {
                continue;
            }
            SHAPE.put(dir, makeShape(dir));
        }
    }

    public DockBlock() {
        super(ModBlockEntities.dock, Properties.of());
        // @formatter:off
        registerDefaultState(defaultBlockState()
            .setValue(ORIENTATION, Direction.NORTH)
            .setValue(HAS_ITEM, false)
            .setValue(IS_ON, false));
        // @formatter:on
    }

    private static VoxelShape makeShape(final Direction direction) {
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
        return ShapeUtils.rotate(shape, Vector3.Y_POS, Vector3.CENTER, direction.toYRot());
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ORIENTATION);
        builder.add(HAS_ITEM);
        builder.add(IS_ON);
    }

    @Override
    public BlockState getStateForPlacement(final @NotNull BlockPlaceContext context) {
        // @formatter:off
        return defaultBlockState()
            .setValue(ORIENTATION, context.getHorizontalDirection())
            .setValue(HAS_ITEM, false)
            .setValue(IS_ON, false);
        // @formatter:on
    }


    @SuppressWarnings("deprecation")
    @Override
    public boolean isPathfindable(final @NotNull BlockState state, final @NotNull BlockGetter world,
                                  final @NotNull BlockPos pos, final @NotNull PathComputationType type) {
        return false;
    }
}
