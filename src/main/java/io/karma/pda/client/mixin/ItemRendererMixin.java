/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.karma.pda.client.ClientEventHandler;
import io.karma.pda.client.event.ItemRenderEvent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Alexander Hinze
 * @since 10/02/2024
 */
@Mixin(ItemRenderer.class)
public final class ItemRendererMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRenderByItemPre(final ItemStack stack, final ItemDisplayContext displayContext,
                                   final boolean isLeftHand, final PoseStack poseStack,
                                   final MultiBufferSource bufferSource, final int packedLight, final int packedOverlay,
                                   final BakedModel model, final CallbackInfo cbi) {
        // @formatter:off
        final var event = new ItemRenderEvent.Pre(stack, displayContext, isLeftHand, poseStack, bufferSource,
            packedLight, packedOverlay, ClientEventHandler.INSTANCE.getFrameTime());
        // @formatter:on
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            cbi.cancel(); // Apply cancel flag from event to control-flow
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderByItemPost(final ItemStack stack, final ItemDisplayContext displayContext,
                                    final boolean isLeftHand, final PoseStack poseStack,
                                    final MultiBufferSource bufferSource, final int packedLight,
                                    final int packedOverlay, final BakedModel model, final CallbackInfo cbi) {
        // @formatter:off
        MinecraftForge.EVENT_BUS.post(new ItemRenderEvent.Post(stack, displayContext, isLeftHand, poseStack,
            bufferSource, packedLight, packedOverlay, ClientEventHandler.INSTANCE.getFrameTime()));
        // @formatter:on
    }
}
