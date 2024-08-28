/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.item;

import io.karma.pda.mod.CommonEventHandler;
import io.karma.pda.mod.client.ClientEventHandler;
import io.karma.pda.mod.client.event.ItemRenderEvent;
import io.karma.pda.mod.client.interaction.PDAInteractionHandler;
import io.karma.pda.mod.client.render.display.DefaultDisplayRenderer;
import io.karma.pda.mod.client.session.ClientSessionHandler;
import io.karma.pda.mod.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * @author Alexander Hinze
 * @since 12/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class PDAItemRenderer {
    public static final PDAItemRenderer INSTANCE = new PDAItemRenderer();

    @Internal
    public void setup() {
        final var forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(this::onRenderItem);
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
        final var buffer = bufferSource.getBuffer(RenderType.solid());
        final var poseStack = event.getPoseStack();
        final var displayContext = event.getDisplayContext();
        final var packedLight = event.getPackedLight();
        final var packedOverlay = event.getPackedOverlay();
        final var hand = event.getHand();

        poseStack.pushPose();

        final var buttonModel = game.getModelManager().getModel(ClientEventHandler.PDA_FULLBRIGHT);
        final var baseModel = game.getModelManager().getModel(ClientEventHandler.PDA_V).applyTransform(displayContext,
            poseStack,
            hand == InteractionHand.OFF_HAND);

        if (displayContext.firstPerson()) {
            poseStack.translate(-0.5,
                -0.5 + PDAInteractionHandler.INSTANCE.getAnimationOffset(hand, event.getFrameTime()),
                -0.5);
        }
        else {
            poseStack.translate(-0.5, -0.5, -0.5);
        }

        final var hasSession = ClientSessionHandler.INSTANCE.findByDevice(stack) != null;
        itemRenderer.renderModelLists(buttonModel,
            stack,
            hasSession ? LightTexture.FULL_BRIGHT : packedLight,
            packedOverlay,
            poseStack,
            buffer);
        itemRenderer.renderModelLists(baseModel, stack, packedLight, packedOverlay, poseStack, buffer);

        //Render out display on top of the baked model dynamically
        final var displayRenderer = DefaultDisplayRenderer.INSTANCE;
        final var player = game.player;
        if (player != null && stack == player.getItemInHand(hand)) {
            final var data = player.getEntityData();
            if (data.hasItem(CommonEventHandler.GLITCH_TICK)) {
                final var glitchTick = data.get(CommonEventHandler.GLITCH_TICK);
                displayRenderer.setGlitchFactor((float) glitchTick / CommonEventHandler.GLITCH_TICKS);
            }
        }
        displayRenderer.renderDisplay(stack, bufferSource, poseStack, packedLight, packedOverlay);
        poseStack.popPose();

        event.setCanceled(true); // Cancel event for PDA item
    }
}
