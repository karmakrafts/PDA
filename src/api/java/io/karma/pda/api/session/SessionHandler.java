/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.session;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
public interface SessionHandler {
    CompletableFuture<Session> createSession(final SessionContext context);

    <S> CompletableFuture<MuxedSession<S>> createSession(final Collection<? extends SelectiveSessionContext<S>> contexts,
                                                         final S initial);

    CompletableFuture<Void> terminateSession(final Session session);

    @Nullable
    Session findById(final UUID id);

    @Nullable
    Session findByDevice(final ItemStack stack);

    @Nullable
    Session findByPosition(final Level level, final BlockPos pos);

    List<Session> findByPlayer(final Player player);

    @Nullable
    Session getActiveSession();

    void setActiveSession(final @Nullable Session session);
}
