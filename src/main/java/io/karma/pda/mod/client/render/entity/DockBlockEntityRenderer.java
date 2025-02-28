/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import io.karma.pda.mod.block.DockBlock;
import io.karma.pda.mod.client.render.display.DefaultDisplayRenderer;
import io.karma.pda.mod.entity.DockBlockEntity;
import io.karma.pda.mod.init.ModBlocks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.TransformationHelper;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 11/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DockBlockEntityRenderer implements BlockEntityRenderer<DockBlockEntity> {
    public DockBlockEntityRenderer(final BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(final @NotNull DockBlockEntity entity,
                       final float partialTick,
                       final @NotNull PoseStack poseStack,
                       final @NotNull MultiBufferSource bufferSource,
                       final int packedLight,
                       final int packedOverlay) {
        if (entity.getItem(0).isEmpty()) {
            return;
        }
        final var world = entity.getLevel();
        if (world == null) {
            return;
        }
        final var state = world.getBlockState(entity.getBlockPos());
        if (state.getBlock() != ModBlocks.dock.get()) {
            return;
        }
        final var stack = entity.getItem(0);
        final var direction = state.getValue(DockBlock.ORIENTATION).getDirection();
        // @formatter:off
        final var angle = direction.getAxis() == Direction.Axis.Z
            ? direction.toYRot() + 180F
            : direction.toYRot();
        // @formatter:on
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F); // Make sure we rotate at center
        poseStack.mulPose(TransformationHelper.quatFromXYZ(0F, angle, 0F, true));
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        poseStack.translate(0F, 3F / 16F, 0F);
        DefaultDisplayRenderer.INSTANCE.renderDisplay(stack, bufferSource, poseStack, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
