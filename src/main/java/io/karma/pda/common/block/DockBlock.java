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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
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
    private static final EnumMap<HorizontalDirection, VoxelShape> EMPTY_SHAPES = new EnumMap<>(HorizontalDirection.class);
    private static final EnumMap<HorizontalDirection, VoxelShape> SHAPES = new EnumMap<>(HorizontalDirection.class);
    private static final EnumMap<HorizontalDirection, VoxelShape> HIT_SHAPES = new EnumMap<>(HorizontalDirection.class);

    static {
        for (final var dir : HorizontalDirection.values()) {
            EMPTY_SHAPES.put(dir, makeEmptyShape(dir));
            SHAPES.put(dir, makeShape(dir));
            HIT_SHAPES.put(dir, makeHitShape(dir));
        }
    }

    public DockBlock() {
        // @formatter:off
        super(ModBlockEntities.dock, Properties.of()
            .dynamicShape()
            .mapColor(MapColor.COLOR_GRAY)
            .forceSolidOn()
            .destroyTime(0.75F));
        registerDefaultState(stateDefinition.any()
            .setValue(ORIENTATION, HorizontalDirection.NORTH)
            .setValue(STATE, State.NO_ITEM));
        // @formatter:on
    }

    private static VoxelShape makeHitShape(final HorizontalDirection direction) {
        final var actualDir = direction.getDirection();
        // @formatter:off
        return ShapeUtils.rotate(Shapes.box(0.1870, 0.1870, 0.370, 0.8130, 0.9380, 0.59380),
            Vector3.Y_POS, Vector3.CENTER, actualDir.getAxis() == Direction.Axis.Z
            ? actualDir.toYRot() + 180F
            : actualDir.toYRot()).optimize();
        // @formatter:on
    }

    private static VoxelShape makeBaseShape() {
        var shape = Shapes.empty();
        shape = Shapes.or(shape, Shapes.box(0.125, 0, 0.3125, 0.875, 0.125, 0.6875));
        shape = Shapes.or(shape, Shapes.box(0.125, 0, 0.125, 0.875, 0.25, 0.3125));
        shape = Shapes.or(shape, Shapes.box(0.125, 0.125, 0.3125, 0.875, 0.1875, 0.375));
        shape = Shapes.or(shape, Shapes.box(0.125, 0.125, 0.625, 0.875, 0.1875, 0.6875));
        shape = Shapes.or(shape, Shapes.box(0.125, 0.125, 0.375, 0.1875, 0.1875, 0.625));
        shape = Shapes.or(shape, Shapes.box(0.8125, 0.125, 0.375, 0.875, 0.1875, 0.625));
        return shape;
    }

    private static VoxelShape makeEmptyShape(final HorizontalDirection direction) {
        var shape = makeBaseShape();
        shape = Shapes.or(shape, Shapes.box(0.28125, 0.125, 0.375, 0.3125, 0.15625, 0.4375));
        shape = Shapes.or(shape, Shapes.box(0.34375, 0.125, 0.375, 0.375, 0.15625, 0.4375));
        shape = Shapes.or(shape, Shapes.box(0.40625, 0.125, 0.375, 0.4375, 0.15625, 0.4375));
        final var actualDir = direction.getDirection();
        // @formatter:off
        return ShapeUtils.rotate(shape, Vector3.Y_POS, Vector3.CENTER, actualDir.getAxis() == Direction.Axis.Z
            ? actualDir.toYRot() + 180F
            : actualDir.toYRot()).optimize();
        // @formatter:on
    }

    private static VoxelShape makeShape(final HorizontalDirection direction) {
        var shape = makeBaseShape();
        shape = Shapes.or(shape, Shapes.box(0.1875, 0.1875, 0.375, 0.8125, 0.9375, 0.59375));
        shape = Shapes.or(shape, Shapes.box(0.25, 0.875, 0.59375, 0.75, 0.9375, 0.625));
        shape = Shapes.or(shape, Shapes.box(0.25, 0.1875, 0.59375, 0.75, 0.3125, 0.625));
        shape = Shapes.or(shape, Shapes.box(0.75, 0.1875, 0.59375, 0.8125, 0.9375, 0.625));
        shape = Shapes.or(shape, Shapes.box(0.1875, 0.1875, 0.59375, 0.25, 0.9375, 0.625));
        final var actualDir = direction.getDirection();
        // @formatter:off
        return ShapeUtils.rotate(shape, Vector3.Y_POS, Vector3.CENTER, actualDir.getAxis() == Direction.Axis.Z
            ? actualDir.toYRot() + 180F
            : actualDir.toYRot()).optimize();
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

        final var entity = world.getBlockEntity(pos);
        if (!(entity instanceof DockBlockEntity dockEntity)) {
            return InteractionResult.FAIL;
        }
        final var stack = dockEntity.getItem(0);
        if (stack.isEmpty()) {
            final var heldItem = player.getItemInHand(hand);
            if (heldItem.isEmpty() || heldItem.getItem() != ModItems.pda.get()) {
                return InteractionResult.FAIL;
            }
            if (!world.isClientSide) {
                dockEntity.setItem(0, heldItem.copy());
                dockEntity.updateBlockState(world, pos);
                heldItem.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public int getLightEmission(final @NotNull BlockState state, final @NotNull BlockGetter world,
                                final @NotNull BlockPos pos) {
        return state.getValue(STATE) == State.ITEM_ON ? 10 : 0;
    }

    @Override
    public boolean isPossibleToRespawnInThis(final @NotNull BlockState state) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(final @NotNull BlockState state, final @NotNull BlockGetter world,
                                        final @NotNull BlockPos pos, final @NotNull CollisionContext context) {
        final var dir = state.getOptionalValue(ORIENTATION).orElse(HorizontalDirection.NORTH);
        if (state.getValue(STATE) != State.NO_ITEM) {
            return SHAPES.get(dir);
        }
        return EMPTY_SHAPES.get(dir);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isPathfindable(final @NotNull BlockState state, final @NotNull BlockGetter world,
                                  final @NotNull BlockPos pos, final @NotNull PathComputationType type) {
        return false;
    }

    public enum State implements StringRepresentable {
        NO_ITEM, ITEM_OFF, ITEM_ON;

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase();
        }
    }
}
