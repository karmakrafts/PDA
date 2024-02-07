package io.karma.pda.common.util;

import io.karma.pda.common.inventory.ItemContainerSupplier;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkHooks;

/**
 * @author Alexander Hinze
 * @since 07/02/2024
 */
public final class PlayerUtils {
    // @formatter:off
    private PlayerUtils() {}
    // @formatter:on

    public static void openMenu(final Player player, final InteractionHand hand,
                                final ItemContainerSupplier<?> supplier) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        final var stack = player.getItemInHand(hand);
        NetworkHooks.openScreen(serverPlayer,
            new SimpleMenuProvider((id, inventory, p) -> supplier.create(id, inventory, stack), Component.empty()),
            buffer -> buffer.writeBoolean(hand == InteractionHand.OFF_HAND));
    }
}
