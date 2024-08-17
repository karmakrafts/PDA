/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.item;

import io.karma.pda.api.util.Constants;
import io.karma.pda.mod.client.ClientEventHandler;
import io.karma.pda.mod.client.event.ItemRenderEvent;
import io.karma.pda.mod.init.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.lwjgl.opengl.GL;

/**
 * @author Alexander Hinze
 * @since 16/08/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DockItemRenderer {
    public static final DockItemRenderer INSTANCE = new DockItemRenderer();
    private static final ModelResourceLocation MODEL = new ModelResourceLocation(Constants.MODID,
        "dock",
        "has_item=false,orientation=south");

    // @formatter:off
    private DockItemRenderer() {}
    // @formatter:on

    @Internal
    public void setup() {
        final var forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(this::onRenderItem);
    }

    private void onRenderItem(final ItemRenderEvent.Pre event) {
        final var stack = event.getStack();

        if (stack.getItem() != ModBlocks.dock.get().asItem()) {
            return;
        }

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
        final var modelManager = game.getModelManager();
        final var fullBrightModel = modelManager.getModel(ClientEventHandler.DOCK_FULLBRIGHT);
        final var model = modelManager.getModel(MODEL).applyTransform(displayContext,
            poseStack,
            hand == InteractionHand.OFF_HAND);
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        itemRenderer.renderModelLists(model, stack, packedLight, packedOverlay, poseStack, buffer);
        itemRenderer.renderModelLists(fullBrightModel,
            stack,
            LightTexture.FULL_BRIGHT,
            packedOverlay,
            poseStack,
            buffer);
        poseStack.popPose();

        event.setCanceled(true); // Cancel vanilla rendering for dock items
    }
}
