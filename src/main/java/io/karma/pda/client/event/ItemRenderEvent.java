package io.karma.pda.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * @author Alexander Hinze
 * @since 10/02/2024
 */
@OnlyIn(Dist.CLIENT)
public class ItemRenderEvent extends Event {
    private final ItemStack stack;
    private final ItemDisplayContext displayContext;
    private final boolean isLeftHand;
    private final PoseStack poseStack;
    private final MultiBufferSource bufferSource;
    private final int packedLight;
    private final int packedOverlay;

    protected ItemRenderEvent(final ItemStack stack, final ItemDisplayContext displayContext, final boolean isLeftHand,
                              final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight,
                              final int packedOverlay) {
        this.stack = stack;
        this.displayContext = displayContext;
        this.isLeftHand = isLeftHand;
        this.poseStack = poseStack;
        this.bufferSource = bufferSource;
        this.packedLight = packedLight;
        this.packedOverlay = packedOverlay;
    }

    public InteractionHand getHand() {
        return isLeftHand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }

    public boolean isLeftHand() {
        return isLeftHand;
    }

    public ItemStack getStack() {
        return stack;
    }

    public ItemDisplayContext getDisplayContext() {
        return displayContext;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public MultiBufferSource getBufferSource() {
        return bufferSource;
    }

    public int getPackedLight() {
        return packedLight;
    }

    public int getPackedOverlay() {
        return packedOverlay;
    }

    @Cancelable
    public static final class Pre extends ItemRenderEvent {
        public Pre(final ItemStack stack, final ItemDisplayContext displayContext, final boolean isLeftHand,
                   final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight,
                   final int packedOverlay) {
            super(stack, displayContext, isLeftHand, poseStack, bufferSource, packedLight, packedOverlay);
        }
    }

    public static final class Post extends ItemRenderEvent {
        public Post(final ItemStack stack, final ItemDisplayContext displayContext, final boolean isLeftHand,
                    final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight,
                    final int packedOverlay) {
            super(stack, displayContext, isLeftHand, poseStack, bufferSource, packedLight, packedOverlay);
        }
    }
}
