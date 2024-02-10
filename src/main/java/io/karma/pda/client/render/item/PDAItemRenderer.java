package io.karma.pda.client.render.item;

import io.karma.pda.client.event.ItemRenderEvent;
import io.karma.pda.client.render.display.DisplayRenderer;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.init.ModItems;
import io.karma.pda.common.item.PDAItem;
import io.karma.pda.common.util.NBTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * @author Alexander Hinze
 * @since 10/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class PDAItemRenderer {
    public static final PDAItemRenderer INSTANCE = new PDAItemRenderer();
    private static final ResourceLocation MODEL_V = new ResourceLocation(PDAMod.MODID, "item/pda_v");
    private static final ResourceLocation MODEL_V_OFF = new ResourceLocation(PDAMod.MODID, "item/pda_v_off");
    private static final ResourceLocation MODEL_H = new ResourceLocation(PDAMod.MODID, "item/pda_h");
    private static final ResourceLocation MODEL_H_OFF = new ResourceLocation(PDAMod.MODID, "item/pda_h_off");

    // @formatter:off
    private PDAItemRenderer() {}
    // @formatter:on

    public void setup() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegisterAdditionalModels);
        MinecraftForge.EVENT_BUS.addListener(this::onRenderItem);
    }

    // Make sure our actual baked models get loaded by the game
    private void onRegisterAdditionalModels(final ModelEvent.RegisterAdditional event) {
        event.register(MODEL_V);
        event.register(MODEL_V_OFF);
        event.register(MODEL_H);
        event.register(MODEL_H_OFF);
    }

    private void onRenderItem(final ItemRenderEvent.Pre event) {
        final var stack = event.getStack();
        if (stack.getItem() != ModItems.pda.get()) {
            return;
        }

        // Determine correct model and render the baked model first like vanilla would do
        final var game = Minecraft.getInstance();
        final var isOn = NBTUtils.getOrDefault(stack.getTag(), PDAItem.TAG_IS_ON, false);
        final var itemRenderer = game.getItemRenderer();
        final var bufferSource = event.getBufferSource();
        final var buffer = bufferSource.getBuffer(RenderType.solid());
        final var poseStack = event.getPoseStack();
        final var displayContext = event.getDisplayContext();
        final var packedLight = event.getPackedLight();
        final var packedOverlay = event.getPackedOverlay();

        poseStack.pushPose();
        // @formatter:off
        final var model = game.getModelManager().getModel(isOn ? MODEL_V : MODEL_V_OFF)
            .applyTransform(displayContext, poseStack, event.isLeftHand());
        poseStack.translate(-0.5, -0.5, -0.5);
        itemRenderer.renderModelLists(model, stack, packedLight, packedOverlay, poseStack, buffer);
        // @formatter:on

        //Render out display on top of the baked model dynamically
        // @formatter:off
        DisplayRenderer.INSTANCE.renderDisplay(stack, displayContext, bufferSource, poseStack, packedLight,
            packedOverlay);
        // @formatter:on
        poseStack.popPose();

        event.setCanceled(true); // Cancel event for PDA item
    }
}
