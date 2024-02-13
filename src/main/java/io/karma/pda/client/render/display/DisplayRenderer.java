package io.karma.pda.client.render.display;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.karma.pda.api.util.Constants;
import io.karma.pda.client.event.RunTickEvent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

/**
 * @author Alexander Hinze
 * @since 09/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DisplayRenderer {
    public static final DisplayRenderer INSTANCE = new DisplayRenderer();

    // Manullay calculated constants for offsets and sizes of the display
    // @formatter:off
    private static final float MIN_X = 0.25F;
    private static final float MIN_Y = 0.125F;
    private static final float OFFSET_Z = 0.609375F;
    private static final float SIZE_X = 0.5F;
    public static final int RES_X = (int) (SIZE_X * 16F) * 16 * 2;
    private static final float SIZE_Y = 0.5625F;
    public static final int RES_Y = (int) (SIZE_Y * 16F) * 16 * 2;
    private static final Matrix4f DISPLAY_PROJECTION_MATRIX = new Matrix4f().ortho2D(0F, RES_X, RES_Y, 0F);
    private static final float MAX_X = MIN_X + SIZE_X;
    private static final float MAX_Y = MIN_Y + SIZE_Y;
    private static final ResourceLocation PIXEL_TEXTURE = new ResourceLocation(Constants.MODID, "textures/pixel.png");

    private static final RenderType RENDER_TYPE = RenderType.create("pda_display",
        DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 4, false, false,
        RenderType.CompositeState.builder()
            .setCullState(RenderStateShard.NO_CULL)
            .setTextureState(new RenderStateShard.EmptyTextureStateShard(
                () -> {
                    RenderSystem.setShaderTexture(0, INSTANCE.framebuffer.getTextureId());
                    RenderSystem.setShaderTexture(1, PIXEL_TEXTURE);
                },
                () -> {
                    RenderSystem.setShaderTexture(0, 0);
                    RenderSystem.setShaderTexture(1, 0);
                }
            ))
            .setShaderState(new RenderStateShard.ShaderStateShard(() -> INSTANCE.shader))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .createCompositeState(false));
    // @formatter:on

    private final BufferBuilder blitBuilder = new BufferBuilder(48);
    private final MultiBufferSource.BufferSource blitBufferSource = MultiBufferSource.immediate(blitBuilder);
    private final HashMap<RenderType, BufferBuilder> displayBuilders = new HashMap<>();
    private final BufferBuilder displayBuilder = new BufferBuilder(1000);
    private final MultiBufferSource.BufferSource displayBufferSource = MultiBufferSource.immediateWithBuffers(
        displayBuilders,
        displayBuilder);
    private ShaderInstance shader;
    private DisplayFramebuffer framebuffer;

    // @formatter:off
    private DisplayRenderer() {}
    // @formatter:on

    @ApiStatus.Internal
    public void setupEarly() {
        MinecraftForge.EVENT_BUS.addListener(this::onRunTickPre);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegisterShaders);
    }

    @ApiStatus.Internal
    public void setup() {
        framebuffer = new DisplayFramebuffer(RES_X, RES_Y); // Adjust framebuffer size
    }

    private void onRegisterShaders(final RegisterShadersEvent event) {
        try {
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                new ResourceLocation("display"),
                DefaultVertexFormat.POSITION_TEX_COLOR), shader -> {
                shader.safeGetUniform("DisplayResolution").set((float) RES_X, (float) RES_Y);
                this.shader = shader;
            });
        }
        catch (Throwable error) {
            error.fillInStackTrace().printStackTrace();
        }
    }

    private void onRunTickPre(final RunTickEvent.Pre event) {
        // Render into the internal framebuffer of the display
        framebuffer.bind();
        GL11.glClearColor(0F, 0F, 0F, 1F);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        final var previousProjectionMatrix = RenderSystem.getProjectionMatrix();
        final var previousVertexSorting = RenderSystem.getVertexSorting();
        RenderSystem.viewport(0, 0, RES_X, RES_Y);
        RenderSystem.setProjectionMatrix(DISPLAY_PROJECTION_MATRIX, VertexSorting.ORTHOGRAPHIC_Z);
        final var poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();
        poseStack.setIdentity();
        RenderSystem.applyModelViewMatrix();

        final var displayBuffer = displayBufferSource.getBuffer(RenderType.gui());
        displayBuffer.vertex(0F, 0F, 0F).color(0xFFFF0000).endVertex();
        displayBuffer.vertex(0F, RES_Y, 0F).color(0xFF00FF00).endVertex();
        displayBuffer.vertex(RES_X, RES_Y, 0F).color(0xFF0000FF).endVertex();
        displayBuffer.vertex(RES_X, 0F, 0F).color(0xFFFFFFFF).endVertex();

        displayBufferSource.endBatch(); // Flush buffer data
        poseStack.popPose();
        RenderSystem.setProjectionMatrix(previousProjectionMatrix, previousVertexSorting);
        framebuffer.unbind();
    }

    @SuppressWarnings("all")
    public void renderDisplay(final MultiBufferSource bufferSource, final PoseStack poseStack) {
        GL11.glFrontFace(GL11.GL_CW);

        // Use an immediate buffer to blit the FBO onto the baked model
        poseStack.pushPose();
        final var matrix = poseStack.last().pose();
        final var blitBuffer = blitBufferSource.getBuffer(RENDER_TYPE);
        blitBuffer.vertex(matrix, MIN_X, MIN_Y, OFFSET_Z).uv(0F, 0F).color(0xFFFFFFFF).endVertex();
        blitBuffer.vertex(matrix, MIN_X, MAX_Y, OFFSET_Z).uv(0F, 1F).color(0xFFFFFFFF).endVertex();
        blitBuffer.vertex(matrix, MAX_X, MAX_Y, OFFSET_Z).uv(1F, 1F).color(0xFFFFFFFF).endVertex();
        blitBuffer.vertex(matrix, MAX_X, MIN_Y, OFFSET_Z).uv(1F, 0F).color(0xFFFFFFFF).endVertex();
        blitBufferSource.endBatch(); // Flush buffer data
        poseStack.popPose();

        GL11.glFrontFace(GL11.GL_CCW);
    }
}
