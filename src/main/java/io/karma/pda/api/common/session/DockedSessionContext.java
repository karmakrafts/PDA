/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.session;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 04/04/2024
 */
public record DockedSessionContext(Player player, BlockPos pos) implements SessionContext {
    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public SessionType getType() {
        return SessionType.DOCKED;
    }

    @Override
    public Level getLevel() {
        return player.level();
    }

    @Override
    public @Nullable BlockPos getPos() {
        return pos;
    }

    @Override
    public @Nullable InteractionHand getHand() {
        return null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(SessionType.DOCKED, player.getUUID(), pos);
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof DockedSessionContext context)) {
            return false;
        }
        // @formatter:off
        return player.getUUID().equals(context.player.getUUID())
            && pos.equals(context.pos);
        // @formatter:on
    }

    @Override
    public String toString() {
        return String.format("DockedSessionContext[%s, %s]", player.getUUID(), pos);
    }
}
