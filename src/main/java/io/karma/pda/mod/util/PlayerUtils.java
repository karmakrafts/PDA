/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.util;

import io.karma.pda.mod.menu.BlockMenuSupplier;
import io.karma.pda.mod.menu.ItemMenuSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkHooks;

/**
 * @author Alexander Hinze
 * @since 07/02/2024
 */
public final class PlayerUtils {
    // @formatter:off
    private PlayerUtils() {}
    // @formatter:on

    public static void openMenu(final Player player, final InteractionHand hand, final ItemMenuSupplier<?> supplier) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        final var stack = player.getItemInHand(hand);
        NetworkHooks.openScreen(serverPlayer,
            new SimpleMenuProvider((id, inventory, p) -> supplier.create(id, inventory, stack), Component.empty()),
            buffer -> buffer.writeBoolean(hand == InteractionHand.OFF_HAND));
    }

    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity & Container> void openMenu(final Player player,
                                                                    final BlockPos pos,
                                                                    final Class<E> entityType,
                                                                    final BlockMenuSupplier<?, E> supplier) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        final var world = serverPlayer.serverLevel();
        final var entity = world.getBlockEntity(pos);
        if (entity == null || !entityType.isAssignableFrom(entity.getClass())) {
            return;
        }
        NetworkHooks.openScreen(serverPlayer,
            new SimpleMenuProvider((id, inventory, p) -> supplier.create(id, inventory, (E) entity), Component.empty()),
            buffer -> buffer.writeBlockPos(pos));
    }

    public static boolean isSame(final Player player1, final Player player2) {
        return player1.getUUID().equals(player2.getUUID());
    }
}
