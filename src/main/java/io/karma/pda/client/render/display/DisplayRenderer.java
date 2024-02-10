package io.karma.pda.client.render.display;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.karma.pda.common.PDAMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

/**
 * @author Alexander Hinze
 * @since 09/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DisplayRenderer {
    public static final DisplayRenderer INSTANCE = new DisplayRenderer();
    // @formatter:off
    private static final RenderType RENDER_TYPE = RenderType.create("pda_display",
        DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 4, false, false,
        RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.EmptyTextureStateShard(
                () -> RenderSystem.setShaderTexture(0, INSTANCE.framebuffer.getTextureId()),
                () -> RenderSystem.setShaderTexture(0, 0)
            ))
            .setShaderState(new RenderStateShard.ShaderStateShard(() -> INSTANCE.shader))
            .createCompositeState(false));
    // @formatter:on

    // Manullay calculated constants for offsets and sizes of the display
    private static final float MIN_X = 0.25F;
    private static final float MIN_Y = 0.125F;
    private static final float OFFSET_Z = 0.609375F;
    private static final float SIZE_X = 0.5F;
    private static final float SIZE_Y = 0.5625F;
    private static final float MAX_X = MIN_X + SIZE_X;
    private static final float MAX_Y = MIN_Y + SIZE_Y;
    private static final int RES_X = (int) (SIZE_X * 16F) * 16 * 2;
    private static final int RES_Y = (int) (SIZE_Y * 16F) * 16 * 2;

    private final PoseStack poseStack = new PoseStack();
    private ShaderInstance shader;
    private DisplayFramebuffer framebuffer;

    // @formatter:off
    private DisplayRenderer() {}
    // @formatter:on

    public void setup() {
        framebuffer = new DisplayFramebuffer(RES_X, RES_Y); // Adjust size
        try {
            final var resourceManager = Minecraft.getInstance().getResourceManager();
            // @formatter:off
            shader = new ShaderInstance(resourceManager, new ResourceLocation(PDAMod.MODID, "display"),
                DefaultVertexFormat.POSITION_TEX_COLOR);
            // @formatter:on
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error("Could not create display renderer shader instance: {}", error.toString());
        }
    }

    public void renderDisplay(final ItemStack stack, final ItemDisplayContext displayContext,
                              final MultiBufferSource bufferSource, final PoseStack poseStack, final int packedLight,
                              final int packedOverlay) {
        framebuffer.bind();
        GL11.glClearColor(0F, 1F, 0F, 1F);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        this.poseStack.pushPose();
        this.poseStack.scale(RES_X, RES_Y, 0F);
        // TODO: render things into display buffer
        this.poseStack.popPose();
        framebuffer.unbind();

        poseStack.pushPose();
        final var matrix = poseStack.last().pose();
        final var buffer = bufferSource.getBuffer(RENDER_TYPE);
        // @formatter:off
        buffer.vertex(matrix, MIN_X, MIN_Y, OFFSET_Z).uv(0F, 0F).color(0xFFFFFFFF).endVertex();
        buffer.vertex(matrix, MAX_X, MIN_Y, OFFSET_Z).uv(1F, 0F).color(0xFFFFFFFF).endVertex();
        buffer.vertex(matrix, MAX_X, MAX_Y, OFFSET_Z).uv(1F, 1F).color(0xFFFFFFFF).endVertex();
        buffer.vertex(matrix, MIN_X, MAX_Y, OFFSET_Z).uv(0F, 1F).color(0xFFFFFFFF).endVertex();
        // @formatter:on
        poseStack.popPose();
    }
}
