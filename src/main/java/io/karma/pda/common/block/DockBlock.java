package io.karma.pda.common.block;

import codechicken.lib.vec.Vector3;
import io.karma.pda.common.entity.DockBlockEntity;
import io.karma.pda.common.init.ModBlockEntities;
import io.karma.pda.common.init.ModItems;
import io.karma.pda.common.menu.DockMenu;
import io.karma.pda.common.util.HorizontalDirection;
import io.karma.pda.common.util.PlayerUtils;
import io.karma.pda.common.util.ShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;

/**
 * @author Alexander Hinze
 * @since 05/02/2024
 */
public final class DockBlock extends BasicEntityBlock<DockBlockEntity> {
    public static final EnumProperty<HorizontalDirection> ORIENTATION = EnumProperty.create("orientation",
        HorizontalDirection.class);
    public static final EnumProperty<State> STATE = EnumProperty.create("state", State.class);
    private static final EnumMap<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

    static {
        for (final var dir : Direction.values()) {
            if (dir.ordinal() < 2) {
                continue;
            }
            SHAPES.put(dir, makeShape(dir));
        }
    }

    public DockBlock() {
        super(ModBlockEntities.dock, Properties.of());
        // @formatter:off
        registerDefaultState(stateDefinition.any()
            .setValue(ORIENTATION, HorizontalDirection.NORTH)
            .setValue(STATE, State.NO_ITEM));
        // @formatter:on
    }

    private static VoxelShape makeShape(final Direction direction) {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.or(shape, Shapes.box(0.125, 0, 0.3125, 0.875, 0.125, 0.6875));
        shape = Shapes.or(shape, Shapes.box(0.125, 0, 0.125, 0.875, 0.25, 0.3125));
        shape = Shapes.or(shape, Shapes.box(0.125, 0.125, 0.3125, 0.875, 0.1875, 0.375));
        shape = Shapes.or(shape, Shapes.box(0.125, 0.125, 0.625, 0.875, 0.1875, 0.6875));
        shape = Shapes.or(shape, Shapes.box(0.125, 0.125, 0.375, 0.1875, 0.1875, 0.625));
        shape = Shapes.or(shape, Shapes.box(0.8125, 0.125, 0.375, 0.875, 0.1875, 0.625));
        shape = Shapes.or(shape, Shapes.box(0.28125, 0.125, 0.375, 0.3125, 0.15625, 0.4375));
        shape = Shapes.or(shape, Shapes.box(0.34375, 0.125, 0.375, 0.375, 0.15625, 0.4375));
        shape = Shapes.or(shape, Shapes.box(0.40625, 0.125, 0.375, 0.4375, 0.15625, 0.4375));
        // @formatter:off
        return ShapeUtils.rotate(shape, Vector3.Y_POS, Vector3.CENTER, direction.getAxis() == Direction.Axis.Z
            ? direction.toYRot() + 180F
            : direction.toYRot()).optimize();
        // @formatter:on
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ORIENTATION);
        builder.add(STATE);
    }

    @Override
    public BlockState getStateForPlacement(final @NotNull BlockPlaceContext context) {
        // @formatter:off
        return defaultBlockState()
            .setValue(ORIENTATION, HorizontalDirection.of(context.getHorizontalDirection()))
            .setValue(STATE, State.NO_ITEM);
        // @formatter:on
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(final @NotNull BlockState state, final @NotNull Level world,
                                          final @NotNull BlockPos pos, final @NotNull Player player,
                                          final @NotNull InteractionHand hand, final @NotNull BlockHitResult hit) {
        if (player.isShiftKeyDown()) {
            PlayerUtils.openMenu(player, pos, DockBlockEntity.class, DockMenu::new);
            return InteractionResult.SUCCESS;
        }
        else {

        }
        return InteractionResult.FAIL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(final @NotNull BlockState state, final @NotNull BlockGetter world,
                                        final @NotNull BlockPos pos, final @NotNull CollisionContext context) {
        return SHAPES.get(world.getBlockState(pos).getOptionalValue(ORIENTATION).orElse(HorizontalDirection.NORTH).getDirection());
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isPathfindable(final @NotNull BlockState state, final @NotNull BlockGetter world,
                                  final @NotNull BlockPos pos, final @NotNull PathComputationType type) {
        return false;
    }

    private void setItem(final Level world, final BlockPos pos, final ItemStack stack) {
        final var entity = world.getBlockEntity(pos);
        if (!(entity instanceof DockBlockEntity dockEntity) || stack.getItem() != ModItems.pda.get()) {
            return;
        }
        dockEntity.setItem(0, stack);
        final var tag = stack.getOrCreateTag();

        world.setBlockAndUpdate(pos, world.getBlockState(pos));
    }

    public enum State implements StringRepresentable {
        NO_ITEM, ITEM_OFF, ITEM_ON;

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase();
        }
    }
}
