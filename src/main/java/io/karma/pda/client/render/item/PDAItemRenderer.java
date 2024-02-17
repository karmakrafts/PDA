package io.karma.pda.client.render.item;

import io.karma.pda.client.ClientEventHandler;
import io.karma.pda.client.event.ItemRenderEvent;
import io.karma.pda.client.render.display.DisplayRenderer;
import io.karma.pda.common.init.ModItems;
import io.karma.pda.common.util.Easings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Alexander Hinze
 * @since 12/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class PDAItemRenderer {
    public static final PDAItemRenderer INSTANCE = new PDAItemRenderer();
    private static final float ANIMATION_STEP = 0.125F;
    private static final float ANIMATION_OFFSET = 6.5F / 16F;

    private final boolean[] isEngaged = new boolean[2];
    private final boolean[] isAnimating = new boolean[2];
    private final float[] animationTick = new float[2];
    private final float[] prevFirstPersonOffset = new float[2];
    private final float[] firstPersonOffset = new float[2];

    // @formatter:off
    private PDAItemRenderer() {}
    // @formatter:on

    @ApiStatus.Internal
    public void setup() {
        final var forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(this::onRenderItem);
        forgeBus.addListener(this::onClientTick);
    }

    private void onClientTick(final TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            for (final var hand : InteractionHand.values()) {
                updateFirstPersonAnimation(hand);
            }
        }
    }

    public void setEngaged(final InteractionHand hand, final boolean isEngaged) {
        this.isEngaged[hand.ordinal()] = isEngaged;
    }

    private void updateFirstPersonAnimation(final InteractionHand hand) {
        final var index = hand.ordinal();
        if (isEngaged[index]) {
            if (animationTick[index] < 1F) {
                prevFirstPersonOffset[index] = firstPersonOffset[index];
                final var currentTick = animationTick[index] = Math.min(1F, animationTick[index] += ANIMATION_STEP);
                firstPersonOffset[index] = Easings.easeOutQuart(currentTick);
                isAnimating[index] = true;
            }
            else {
                isAnimating[index] = false;
            }
        }
        else {
            if (animationTick[index] > 0F) {
                prevFirstPersonOffset[index] = firstPersonOffset[index];
                final var currentTick = animationTick[index] = Math.max(0F, animationTick[index] -= ANIMATION_STEP);
                firstPersonOffset[index] = Easings.easeInQuart(currentTick);
                isAnimating[index] = true;
            }
            else {
                isAnimating[index] = false;
            }
        }
    }

    private float getAnimationOffset(final InteractionHand hand, final float partialTick) {
        final var index = hand.ordinal();
        var offset = firstPersonOffset[index];
        if (isAnimating[index]) {
            final var prevOffset = prevFirstPersonOffset[hand.ordinal()];
            offset = prevOffset + partialTick * (offset - prevOffset);
        }
        return offset * ANIMATION_OFFSET;
    }

    private void onRenderItem(final ItemRenderEvent.Pre event) {
        final var stack = event.getStack();
        if (stack.getItem() != ModItems.pda.get()) {
            return;
        }

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
        final var model = game.getModelManager().getModel(ClientEventHandler.PDA_MODEL_DISENGAGED)
            .applyTransform(displayContext, poseStack, hand == InteractionHand.OFF_HAND);
        // @formatter:on
        if (displayContext.firstPerson()) {
            poseStack.translate(-0.5, -0.5 + getAnimationOffset(hand, event.getPartialTick()), -0.5);
        }
        else {
            poseStack.translate(-0.5, -0.5, -0.5);
        }
        itemRenderer.renderModelLists(model, stack, packedLight, packedOverlay, poseStack, buffer);
        //Render out display on top of the baked model dynamically
        DisplayRenderer.INSTANCE.renderDisplay(bufferSource, poseStack);
        poseStack.popPose();

        event.setCanceled(true); // Cancel event for PDA item
    }
}
