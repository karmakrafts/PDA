/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.display;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.karma.pda.api.common.util.Constants;
import io.karma.pda.api.common.util.DisplayType;
import io.karma.pda.client.ClientEventHandler;
import io.karma.pda.common.PDAMod;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 09/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DisplayRenderer {
    private static final DisplayRenderer INSTANCE = new DisplayRenderer();
    private static final int GLITCH_TICKS = 10;
    public static final float MIN_X = 0.25F;
    public static final float MIN_Y = 0.125F;
    public static final float OFFSET_Z = 0.609375F;
    private static final float SIZE_X = 0.5F;
    public static final int RES_X = ((int) (SIZE_X * 16F) * 16) << 1; // 256
    private static final float SIZE_Y = 0.5625F;
    public static final int RES_Y = ((int) (SIZE_Y * 16F) * 16) << 1; // 288
    public static final float MAX_X = MIN_X + SIZE_X;
    public static final float MAX_Y = MIN_Y + SIZE_Y;
    private static final Matrix4f IDENTITY_MATRIX = new Matrix4f().identity();
    private static final Matrix4f DISPLAY_PROJECTION_MATRIX = new Matrix4f().ortho2D(0F, RES_X, RES_Y, 0F);
    private static final ResourceLocation PIXEL_TEXTURE = new ResourceLocation(Constants.MODID, "textures/pixel.png");
    private static ShaderInstance BLIT_BW_SHADER;
    private static ShaderInstance BLIT_SRGB_SHADER;
    private static ShaderInstance BLIT_OLED_SHADER;
    private static ShaderInstance COLOR_SHADER;
    private static ShaderInstance TEXTURE_SHADER;

    private static final RenderStateShard.OutputStateShard DISPLAY_OUTPUT = new RenderStateShard.OutputStateShard(
        "display_output",
        INSTANCE::setupDisplayOutput,
        INSTANCE::resetDisplayOutput);

    // @formatter:off
    private static final RenderType COLOR_RENDER_TYPE = RenderType.create("pda_display_color",
        DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 4, false, false,
        RenderType.CompositeState.builder()
            .setCullState(RenderStateShard.CULL)
            .setShaderState(new RenderStateShard.ShaderStateShard(DisplayRenderer::getColorShader))
            .setOutputState(DISPLAY_OUTPUT)
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .createCompositeState(false));

    private static final RenderType COLOR_TEX_RENDER_TYPE = RenderType.create("pda_display_color_tex",
        DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 4, false, false,
        RenderType.CompositeState.builder()
            .setCullState(RenderStateShard.CULL)
            .setShaderState(new RenderStateShard.ShaderStateShard(DisplayRenderer::getColorTexShader))
            .setOutputState(DISPLAY_OUTPUT)
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .createCompositeState(false));
    // @formatter:on

    private static final RenderType BLIT_BW_RENDER_TYPE = createBlitRenderType("bw", DisplayRenderer::getBlitBWShader);
    private static final RenderType BLIT_SRGB_RENDER_TYPE = createBlitRenderType("srgb",
        DisplayRenderer::getBlitSRGBShader);
    private static final RenderType BLIT_OLED_RENDER_TYPE = createBlitRenderType("oled",
        DisplayRenderer::getBlitOLEDShader);

    private static RenderType createBlitRenderType(final String name, final Supplier<ShaderInstance> shaderSupplier) {
        // @formatter:off
        return RenderType.create(String.format("%s:display_blit_%s", Constants.MODID, name),
            DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 4, false, false,
            RenderType.CompositeState.builder()
                .setCullState(RenderStateShard.CULL)
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
                .setShaderState(new RenderStateShard.ShaderStateShard(shaderSupplier))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .createCompositeState(false));
        // @formatter:on
    }

    private final BufferBuilder blitBuilder = new BufferBuilder(48);
    private final MultiBufferSource.BufferSource blitBufferSource = MultiBufferSource.immediate(blitBuilder);
    private final HashMap<RenderType, BufferBuilder> displayBuilders = new HashMap<>();
    private final BufferBuilder displayBuilder = new BufferBuilder(1000);
    private final MultiBufferSource.BufferSource displayBufferSource = MultiBufferSource.immediateWithBuffers(
        displayBuilders,
        displayBuilder);

    private final int[] prevViewport = new int[4]; // Viewport position/size from last frame
    private final PoseStack displayPoseStack = new PoseStack();
    private DisplayFramebuffer framebuffer;
    private Matrix4f prevProjectionMatrix;
    private VertexSorting prevVertexSorting;
    private Matrix4f prevModelViewMatrix;
    private float glitchFactor;

    private DisplayRenderer() {
        displayPoseStack.setIdentity();
    }

    public static DisplayRenderer getInstance() {
        return INSTANCE.reset();
    }

    private DisplayRenderer reset() {
        glitchFactor = 0F;
        return this;
    }

    public void setGlitchFactor(final float glitchFactor) {
        this.glitchFactor = glitchFactor;
    }

    @ApiStatus.Internal
    public void setupEarly() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegisterShaders);
    }

    @ApiStatus.Internal
    public void setup() {
        framebuffer = new DisplayFramebuffer(RES_X, RES_Y); // Adjust framebuffer size
        PDAMod.DISPOSITION_HANDLER.addObject(framebuffer);
    }

    private void onRegisterShaders(final RegisterShadersEvent event) {
        try {
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                new ResourceLocation(Constants.MODID, "display_blit_bw"),
                DefaultVertexFormat.POSITION_TEX_COLOR), shader -> {
                BLIT_BW_SHADER = shader;
            });
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                new ResourceLocation(Constants.MODID, "display_blit_srgb"),
                DefaultVertexFormat.POSITION_TEX_COLOR), shader -> {
                BLIT_SRGB_SHADER = shader;
            });
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                new ResourceLocation(Constants.MODID, "display_blit_oled"),
                DefaultVertexFormat.POSITION_TEX_COLOR), shader -> {
                BLIT_OLED_SHADER = shader;
            });
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                new ResourceLocation(Constants.MODID, "display_color"),
                DefaultVertexFormat.POSITION_COLOR), shader -> COLOR_SHADER = shader);
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                new ResourceLocation(Constants.MODID, "display_color_tex"),
                DefaultVertexFormat.POSITION_TEX_COLOR), shader -> TEXTURE_SHADER = shader);
        }
        catch (Throwable error) {
            error.fillInStackTrace().printStackTrace();
        }
    }

    private void setupDisplayOutput() {
        framebuffer.bind();

        prevProjectionMatrix = RenderSystem.getProjectionMatrix();
        prevVertexSorting = RenderSystem.getVertexSorting();
        RenderSystem.setProjectionMatrix(DISPLAY_PROJECTION_MATRIX, VertexSorting.ORTHOGRAPHIC_Z);
        prevModelViewMatrix = RenderSystem.modelViewMatrix;
        RenderSystem.modelViewMatrix = IDENTITY_MATRIX;

        GL11.glGetIntegerv(GL11.GL_VIEWPORT, prevViewport);
        GL11.glViewport(0, 0, RES_X, RES_Y);
        GL11.glClearColor(1F, 0F, 0F, 1F);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    private void resetDisplayOutput() {
        GL11.glViewport(prevViewport[0], prevViewport[1], prevViewport[2], prevViewport[3]);
        RenderSystem.setProjectionMatrix(prevProjectionMatrix, prevVertexSorting);
        RenderSystem.modelViewMatrix = prevModelViewMatrix;
        framebuffer.unbind();
    }

    @SuppressWarnings("all")
    private static ShaderInstance getBlitBWShader() {
        BLIT_BW_SHADER.getUniform("Time").set(ClientEventHandler.INSTANCE.getShaderTime());
        BLIT_BW_SHADER.getUniform("GlitchFactor").set(INSTANCE.glitchFactor);
        return BLIT_BW_SHADER;
    }

    @SuppressWarnings("all")
    private static ShaderInstance getBlitSRGBShader() {
        BLIT_SRGB_SHADER.getUniform("Time").set(ClientEventHandler.INSTANCE.getShaderTime());
        BLIT_SRGB_SHADER.getUniform("GlitchFactor").set(INSTANCE.glitchFactor);
        return BLIT_SRGB_SHADER;
    }

    @SuppressWarnings("all")
    private static ShaderInstance getBlitOLEDShader() {
        BLIT_OLED_SHADER.getUniform("Time").set(ClientEventHandler.INSTANCE.getShaderTime());
        BLIT_OLED_SHADER.getUniform("GlitchFactor").set(INSTANCE.glitchFactor);
        return BLIT_OLED_SHADER;
    }

    private static ShaderInstance getColorShader() {
        return COLOR_SHADER;
    }

    private static ShaderInstance getColorTexShader() {
        return TEXTURE_SHADER;
    }

    private void renderIntoDisplayBuffer() {
        displayPoseStack.pushPose();

        final var matrix = displayPoseStack.last().pose();
        final var buffer = displayBufferSource.getBuffer(COLOR_RENDER_TYPE);
        buffer.vertex(matrix, 0F, 0F, 0F).color(1F, 0F, 0F, 1F).endVertex();
        buffer.vertex(matrix, 0F, RES_Y, 0F).color(0F, 1F, 0F, 1F).endVertex();
        buffer.vertex(matrix, RES_X, RES_Y, 0F).color(0F, 0F, 1F, 1F).endVertex();
        buffer.vertex(matrix, RES_X, 0F, 0F).color(1F, 1F, 1F, 1F).endVertex();

        displayBufferSource.endBatch();
        displayPoseStack.popPose();
    }

    private static RenderType getBlitRenderType(final DisplayType type) {
        return switch (type) {
            case SRGB -> BLIT_SRGB_RENDER_TYPE;
            case OLED -> BLIT_OLED_RENDER_TYPE;
            default -> BLIT_BW_RENDER_TYPE;
        };
    }

    @SuppressWarnings("all")
    public void renderDisplay(final MultiBufferSource bufferSource, final PoseStack poseStack, final DisplayType type) {
        renderIntoDisplayBuffer(); // TODO: make this happen for every active PDA

        // Use an immediate buffer to blit the FBO onto the baked model
        final var matrix = poseStack.last().pose();
        final var buffer = blitBufferSource.getBuffer(getBlitRenderType(type));
        buffer.vertex(matrix, MIN_X, MIN_Y, OFFSET_Z).uv(0F, 0F).color(0xFFFFFFFF).endVertex();
        buffer.vertex(matrix, MIN_X, MAX_Y, OFFSET_Z).uv(0F, 1F).color(0xFFFFFFFF).endVertex();
        buffer.vertex(matrix, MAX_X, MAX_Y, OFFSET_Z).uv(1F, 1F).color(0xFFFFFFFF).endVertex();
        buffer.vertex(matrix, MAX_X, MIN_Y, OFFSET_Z).uv(1F, 0F).color(0xFFFFFFFF).endVertex();

        final var prevFrontFace = GL11.glGetInteger(GL11.GL_FRONT_FACE);
        GL11.glFrontFace(GL11.GL_CW);
        blitBufferSource.endBatch(); // Flush buffer data
        GL11.glFrontFace(prevFrontFace);
    }
}
