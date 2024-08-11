/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.session;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * The instance of this type associated with every session instance
 * allows accessing information relevant to the current session like
 * the user, the session type, the block entity position if in docked mode
 * and the {@link InteractionHand} when in handheld mode.
 * <p>
 * Implementations of this interface should either be declared as a record
 * type (to avoid extra boilerplate code) or they should explicitly implement
 * {@code toString()}, {@code equals()} and {@code hashCode()} since they are
 * used as keys to hash tables internally.
 *
 * @author Alexander Hinze
 * @since 04/04/2024
 */
public interface SessionContext {
    SessionType getType();

    ItemStack getDeviceItem();

    Player getPlayer();

    Level getLevel();

    @Nullable
    BlockPos getPos();

    @Nullable
    InteractionHand getHand();

    default ItemStack getItem() {
        final var hand = getHand();
        if (hand == null) {
            return ItemStack.EMPTY;
        }
        return getPlayer().getItemInHand(hand);
    }

    default BlockState getState() {
        final var pos = getPos();
        if (pos == null) {
            return Blocks.AIR.defaultBlockState();
        }
        return getLevel().getBlockState(pos);
    }

    default @Nullable BlockEntity getBlockEntity() {
        final var pos = getPos();
        if (pos == null) {
            return null;
        }
        return getLevel().getBlockEntity(pos);
    }
}
