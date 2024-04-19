/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.item;

import io.karma.pda.api.common.util.DisplayType;
import io.karma.pda.client.ClientEventHandler;
import io.karma.pda.client.event.ItemRenderEvent;
import io.karma.pda.client.render.display.DisplayRenderer;
import io.karma.pda.client.screen.PDAScreen;
import io.karma.pda.common.CommonEventHandler;
import io.karma.pda.common.init.ModItems;
import io.karma.pda.common.item.PDAItem;
import io.karma.pda.common.util.Easings;
import io.karma.pda.common.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 12/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class PDAItemRenderer {
    public static final PDAItemRenderer INSTANCE = new PDAItemRenderer();
    private static final float ANIMATION_OFFSET = 6.5F / 16F;
    private static final int ANIMATION_TICKS = 10;

    private final int[] animationTick = new int[InteractionHand.values().length];
    private final float[] previousOffset = new float[InteractionHand.values().length];
    private final float[] offset = new float[InteractionHand.values().length];
    private final boolean[] isEngaged = new boolean[InteractionHand.values().length];

    @ApiStatus.Internal
    public void setup() {
        final var forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(this::onRenderItem);
        forgeBus.addListener(this::onClientTick);
        forgeBus.addListener(this::onLivingDeath);
        forgeBus.addListener(this::onLivingDamage);
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

    private void onLivingDeath(final LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (PlayerUtils.isSame(player, Objects.requireNonNull(Minecraft.getInstance().player))) {
            resetAnimation();
        }
    }

    private void onLivingDamage(final LivingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (PlayerUtils.isSame(player, Objects.requireNonNull(Minecraft.getInstance().player))) {
            resetAnimation();
        }
    }

    private void onPlayerChangeDimension(final PlayerEvent.PlayerChangedDimensionEvent event) {
        if (PlayerUtils.isSame(event.getEntity(), Objects.requireNonNull(Minecraft.getInstance().player))) {
            resetAnimation();
        }
    }

    private void onEntityTeleport(final EntityTeleportEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (PlayerUtils.isSame(player, Objects.requireNonNull(Minecraft.getInstance().player))) {
            resetAnimation();
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

    private float getAnimationOffset(final InteractionHand hand, final float frameTime) {
        final var index = hand.ordinal();
        final var tick = animationTick[index];
        final var current = offset[index];
        if (tick == 0 || tick == ANIMATION_TICKS) {
            return current * ANIMATION_OFFSET;
        }
        final var previous = previousOffset[index];
        return Math.fma(frameTime, (current - previous), previous) * ANIMATION_OFFSET;
    }

    private void onRenderItem(final ItemRenderEvent.Pre event) {
        final var stack = event.getStack();
        if (stack.getItem() != ModItems.pda.get()) {
            return;
        }
        final var displayType = PDAItem.getDisplayType(stack).orElse(DisplayType.BW);

        // Determine correct model and render the baked model first like vanilla would do
        final var game = Minecraft.getInstance();
        final var itemRenderer = game.getItemRenderer();
        final var bufferSource = event.getBufferSource();
        final var buffer = bufferSource.getBuffer(RenderType.translucent());
        final var poseStack = event.getPoseStack();
        final var displayContext = event.getDisplayContext();
        final var packedLight = event.getPackedLight();
        final var packedOverlay = event.getPackedOverlay();
        final var hand = event.getHand();

        poseStack.pushPose();
        // @formatter:off
        final var model = game.getModelManager().getModel(ClientEventHandler.PDA_MODEL_V)
            .applyTransform(displayContext, poseStack, hand == InteractionHand.OFF_HAND);
        // @formatter:on
        if (displayContext.firstPerson()) {
            poseStack.translate(-0.5, -0.5 + getAnimationOffset(hand, event.getFrameTime()), -0.5);
        }
        else {
            poseStack.translate(-0.5, -0.5, -0.5);
        }
        itemRenderer.renderModelLists(model, stack, packedLight, packedOverlay, poseStack, buffer);
        //Render out display on top of the baked model dynamically
        final var displayRenderer = DisplayRenderer.getInstance();
        final var player = game.player;
        if (player != null && stack == player.getItemInHand(hand)) {
            final var data = player.getEntityData();
            if (data.hasItem(CommonEventHandler.GLITCH_TICK)) {
                final var glitchTick = data.get(CommonEventHandler.GLITCH_TICK);
                displayRenderer.setGlitchFactor((float) glitchTick / CommonEventHandler.GLITCH_TICKS);
            }
        }
        displayRenderer.renderDisplay(stack, bufferSource, poseStack, displayType);
        poseStack.popPose();

        event.setCanceled(true); // Cancel event for PDA item
    }
}
