/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.block;

import io.karma.pda.api.app.DefaultApps;
import io.karma.pda.api.util.Constants;
import io.karma.pda.api.util.MathUtils;
import io.karma.pda.mod.client.screen.DockScreen;
import io.karma.pda.mod.client.session.ClientSessionHandler;
import io.karma.pda.mod.entity.DockBlockEntity;
import io.karma.pda.mod.init.ModBlockEntities;
import io.karma.pda.mod.init.ModItems;
import io.karma.pda.mod.menu.DockStorageMenu;
import io.karma.pda.mod.session.DockedSessionContext;
import io.karma.pda.mod.util.HorizontalDirection;
import io.karma.pda.mod.util.PlayerUtils;
import io.karma.pda.mod.util.ShapeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.List;
import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 05/02/2024
 */
public final class DockBlock extends BasicEntityBlock<DockBlockEntity> {
    public static final EnumProperty<HorizontalDirection> ORIENTATION = EnumProperty.create("orientation",
        HorizontalDirection.class);
    public static final BooleanProperty HAS_ITEM = BooleanProperty.create("has_item");
    public static final EnumMap<HorizontalDirection, VoxelShape> SHAPES = new EnumMap<>(HorizontalDirection.class);
    private static final EnumMap<HorizontalDirection, VoxelShape> EMPTY_SHAPES = new EnumMap<>(HorizontalDirection.class);

    static {
        for (final var dir : HorizontalDirection.values()) {
            EMPTY_SHAPES.put(dir, makeEmptyShape(dir));
            SHAPES.put(dir, makeShape(dir));
        }
    }

    public DockBlock() {
        // @formatter:off
        super(ModBlockEntities.dock, Properties.of()
            .sound(SoundType.METAL)
            .dynamicShape()
            .mapColor(MapColor.COLOR_GRAY)
            .forceSolidOn()
            .destroyTime(0.75F));
        registerDefaultState(stateDefinition.any()
            .setValue(ORIENTATION, HorizontalDirection.NORTH)
            .setValue(HAS_ITEM, false));
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
        return ShapeUtils.rotate(shape, MathUtils.Y_POS, MathUtils.CENTER, actualDir.getAxis() == Direction.Axis.Z
            ? actualDir.toYRot() + 180F
            : actualDir.toYRot());
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
        return ShapeUtils.rotate(shape, MathUtils.Y_POS, MathUtils.CENTER, actualDir.getAxis() == Direction.Axis.Z
            ? actualDir.toYRot() + 180F
            : actualDir.toYRot());
        // @formatter:on
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ORIENTATION);
        builder.add(HAS_ITEM);
    }

    @Override
    public BlockState getStateForPlacement(final @NotNull BlockPlaceContext context) {
        // @formatter:off
        return defaultBlockState()
            .setValue(ORIENTATION, HorizontalDirection.of(context.getHorizontalDirection()))
            .setValue(HAS_ITEM, false);
        // @formatter:on
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull List<ItemStack> getDrops(final @NotNull BlockState state,
                                             final @NotNull LootParams.Builder builder) {
        final var blockEntity = builder.getParameter(LootContextParams.BLOCK_ENTITY);
        if (!(blockEntity instanceof DockBlockEntity dockEntity)) {
            return super.getDrops(state, builder);
        }
        builder.withDynamicDrop(new ResourceLocation(Constants.MODID, "dock"), consumer -> {
            consumer.accept(new ItemStack(this));
            consumer.accept(dockEntity.getItem(0));
        });
        return super.getDrops(state, builder);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(final @NotNull BlockState state,
                                          final @NotNull Level world,
                                          final @NotNull BlockPos pos,
                                          final @NotNull Player player,
                                          final @NotNull InteractionHand hand,
                                          final @NotNull BlockHitResult hit) {
        // Open the storage menu if the player is sneaking
        if (player.isShiftKeyDown()) {
            PlayerUtils.openMenu(player, pos, DockBlockEntity.class, DockStorageMenu::new);
            return InteractionResult.SUCCESS;
        }
        // Handle item in-world interaction when placing a PDA into the dock
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
                heldItem.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        // If we already have an item, open the dock screen on the client only
        else if (world.isClientSide) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> openScreen(player, pos));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @OnlyIn(Dist.CLIENT)
    private void openScreen(final Player player, final BlockPos pos) {
        final var game = Minecraft.getInstance();
        if (game.player != player || !game.options.getCameraType().isFirstPerson()) {
            return;
        }
        final var sessionHandler = ClientSessionHandler.INSTANCE;
        // @formatter:off
        sessionHandler.createSession(new DockedSessionContext(player, pos)).thenAccept(session -> {
            session.getLauncher().openApp(DefaultApps.LAUNCHER).join();
            sessionHandler.setActiveSession(session);
            game.execute(() -> {
                game.setScreen(new DockScreen(pos, sessionHandler.getActiveSession()));
                Objects.requireNonNull(player.level()).playSound(
                    player, pos, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundSource.AMBIENT, 0.3F, 1.75F);
            });
        });
        // @formatter:on
    }

    @Override
    public boolean isPossibleToRespawnInThis(final @NotNull BlockState state) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(final @NotNull BlockState state,
                                        final @NotNull BlockGetter world,
                                        final @NotNull BlockPos pos,
                                        final @NotNull CollisionContext context) {
        final var dir = state.getOptionalValue(ORIENTATION).orElse(HorizontalDirection.NORTH);
        if (state.getValue(HAS_ITEM)) {
            return SHAPES.get(dir);
        }
        return EMPTY_SHAPES.get(dir);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isPathfindable(final @NotNull BlockState state,
                                  final @NotNull BlockGetter world,
                                  final @NotNull BlockPos pos,
                                  final @NotNull PathComputationType type) {
        return false;
    }
}
