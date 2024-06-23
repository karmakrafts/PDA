/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.interaction;

import io.karma.pda.mod.client.screen.PDAScreen;
import io.karma.pda.mod.client.session.ClientSessionHandler;
import io.karma.pda.mod.item.PDAItem;
import io.karma.pda.mod.util.Easings;
import io.karma.pda.mod.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 19/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class PDAInteractionHandler {
    public static final PDAInteractionHandler INSTANCE = new PDAInteractionHandler();
    private static final float ANIMATION_OFFSET = 6.5F / 16F;
    private static final int ANIMATION_TICKS = 10;

    private final int[] animationTick = new int[InteractionHand.values().length];
    private final float[] previousOffset = new float[InteractionHand.values().length];
    private final float[] offset = new float[InteractionHand.values().length];
    private final boolean[] isEngaged = new boolean[InteractionHand.values().length];

    // @formatter:off
    private PDAInteractionHandler() {}
    // @formatter:on

    @ApiStatus.Internal
    public void setup() {
        final var forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(this::onClientTick);
        forgeBus.addListener(this::onLivingDeath);
        forgeBus.addListener(this::onPlayerChangeDimension);
        forgeBus.addListener(this::onEntityTeleport);
    }

    public void setEngaged(final InteractionHand hand, final boolean isEngaged) {
        this.isEngaged[hand.ordinal()] = isEngaged;
    }

    private void onClientTick(final TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            for (final var hand : InteractionHand.values()) {
                updateAnimation(hand);
            }
        }
    }

    private void reset() {
        resetAnimation();
        PDAItem.isScreenOpen = false;
        final var sessionHandler = ClientSessionHandler.INSTANCE;
        final var session = sessionHandler.getActiveSession();
        if (session == null) {
            return;
        }
        sessionHandler.terminateSession(session).thenAccept(v -> {
            sessionHandler.setActiveSession(null); // Reset client session
        });
    }

    private void onLivingDeath(final LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (PlayerUtils.isSame(player, Objects.requireNonNull(Minecraft.getInstance().player))) {
            reset();
        }
    }

    private void onPlayerChangeDimension(final PlayerEvent.PlayerChangedDimensionEvent event) {
        if (PlayerUtils.isSame(event.getEntity(), Objects.requireNonNull(Minecraft.getInstance().player))) {
            reset();
        }
    }

    private void onEntityTeleport(final EntityTeleportEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (PlayerUtils.isSame(player, Objects.requireNonNull(Minecraft.getInstance().player))) {
            reset();
        }
    }

    private void updateAnimation(final InteractionHand hand) {
        final var index = hand.ordinal();
        final var tick = animationTick[index];
        if (isEngaged[index]) {
            if (tick < ANIMATION_TICKS) {
                previousOffset[index] = offset[index];
                offset[index] = Easings.easeOutQuart((float) tick / ANIMATION_TICKS);
                animationTick[index]++;
            }
        }
        else {
            if (tick > 0) {
                previousOffset[index] = offset[index];
                offset[index] = Easings.easeInQuart((float) tick / ANIMATION_TICKS);
                animationTick[index]--;
            }
        }
    }

    private void resetAnimation() {
        final var game = Minecraft.getInstance();
        game.execute(() -> {
            if (game.screen instanceof PDAScreen) {
                game.popGuiLayer();
            }
            for (final var hand : InteractionHand.values()) {
                final var index = hand.ordinal();
                animationTick[index] = 0;
                offset[index] = 0;
                previousOffset[index] = 0;
                isEngaged[index] = false;
            }
        });
    }

    public float getAnimationOffset(final InteractionHand hand, final float frameTime) {
        final var index = hand.ordinal();
        final var tick = animationTick[index];
        final var current = offset[index];
        if (tick == 0 || tick == ANIMATION_TICKS) {
            return current * ANIMATION_OFFSET;
        }
        final var previous = previousOffset[index];
        return Math.fma(frameTime, (current - previous), previous) * ANIMATION_OFFSET;
    }
}
