/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.session;

import io.karma.pda.api.session.SelectiveSessionContext;
import io.karma.pda.api.session.SessionType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 04/04/2024
 */
public record HandheldSessionContext(Player player, InteractionHand hand)
    implements SelectiveSessionContext<InteractionHand> {
    @Override
    public ItemStack getDeviceItem() {
        return player.getItemInHand(hand);
    }

    @Override
    public InteractionHand getSelector() {
        return hand;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public SessionType getType() {
        return hand == InteractionHand.MAIN_HAND ? SessionType.HANDHELD_MAIN : SessionType.HANDHELD_OFF;
    }

    @Override
    public Level getLevel() {
        return player.level();
    }

    @Override
    public @Nullable BlockPos getPos() {
        return null;
    }

    @Override
    public @Nullable InteractionHand getHand() {
        return hand;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), player.getUUID(), hand);
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof HandheldSessionContext context)) {
            return false;
        }
        // @formatter:off
        return getType() == context.getType()
            && player.getUUID().equals(context.player.getUUID())
            && hand == context.hand;
        // @formatter:on
    }

    @Override
    public String toString() {
        return String.format("HandheldSessionContext[%s, %s, %s]", getType(), player.getUUID(), hand);
    }
}
