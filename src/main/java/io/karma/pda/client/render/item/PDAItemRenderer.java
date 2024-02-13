package io.karma.pda.client.render.item;

import io.karma.pda.client.ClientEventHandler;
import io.karma.pda.client.event.ItemRenderEvent;
import io.karma.pda.client.render.display.DisplayRenderer;
import io.karma.pda.common.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Alexander Hinze
 * @since 12/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class PDAItemRenderer {
    public static final PDAItemRenderer INSTANCE = new PDAItemRenderer();
    private static final float ANIMATION_STEP = 0.1F;
    private static final float ANIMATION_OFFSET = 6.5F / 16F;

    private final boolean[] isEngaged = new boolean[2];
    private final float[] firstPersonOffset = new float[2];

    // @formatter:off
    private PDAItemRenderer() {}
    // @formatter:on

    @ApiStatus.Internal
    public void setup() {
        MinecraftForge.EVENT_BUS.addListener(this::onRenderItem);
    }

    public void setEngaged(final InteractionHand hand, final boolean isEngaged) {
        this.isEngaged[hand.ordinal()] = isEngaged;
    }

    private void updateFirstPersonAnimation(final InteractionHand hand) {
        final var index = hand.ordinal();
        if (isEngaged[index]) {
            if (firstPersonOffset[index] < 1F) {
                firstPersonOffset[index] += ANIMATION_STEP;
            }
        }
        else {
            if (firstPersonOffset[index] > 0F) {
                firstPersonOffset[index] -= ANIMATION_STEP;
            }
        }
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
        updateFirstPersonAnimation(hand);
        if (displayContext.firstPerson()) {
            final var offset = ANIMATION_OFFSET * firstPersonOffset[hand.ordinal()];
            poseStack.translate(-0.5, -0.5 + offset, -0.5);
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
