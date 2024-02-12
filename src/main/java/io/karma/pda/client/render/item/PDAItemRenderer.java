package io.karma.pda.client.render.item;

import io.karma.pda.client.event.ItemRenderEvent;
import io.karma.pda.client.render.display.DisplayRenderer;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Alexander Hinze
 * @since 10/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class PDAItemRenderer {
    public static final PDAItemRenderer INSTANCE = new PDAItemRenderer();
    private static final ResourceLocation MODEL_BASE = new ResourceLocation(PDAMod.MODID, "item/pda_base");

    // @formatter:off
    private PDAItemRenderer() {}
    // @formatter:on

    @ApiStatus.Internal
    public void setupEarly() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegisterAdditionalModels);
        MinecraftForge.EVENT_BUS.addListener(this::onRenderItem);
    }

    // Make sure our actual baked models get loaded by the game
    private void onRegisterAdditionalModels(final ModelEvent.RegisterAdditional event) {
        event.register(MODEL_BASE);
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

        poseStack.pushPose();
        // @formatter:off
        final var model = game.getModelManager().getModel(MODEL_BASE)
            .applyTransform(displayContext, poseStack, event.isLeftHand());
        poseStack.translate(-0.5, -0.5, -0.5);
        itemRenderer.renderModelLists(model, stack, packedLight, packedOverlay, poseStack, buffer);
        // @formatter:on
        //Render out display on top of the baked model dynamically
        DisplayRenderer.INSTANCE.renderDisplay(bufferSource, poseStack);
        poseStack.popPose();

        event.setCanceled(true); // Cancel event for PDA item
    }
}
