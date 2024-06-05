/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.display;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import io.karma.pda.api.client.render.app.AppRenderers;
import io.karma.pda.api.client.render.display.DisplayMode;
import io.karma.pda.api.client.render.display.DisplayRenderer;
import io.karma.pda.api.common.app.App;
import io.karma.pda.api.common.app.AppType;
import io.karma.pda.api.common.display.DisplayModeSpec;
import io.karma.pda.api.common.display.DisplayResolution;
import io.karma.pda.client.render.graphics.DefaultGraphics;
import io.karma.pda.client.render.graphics.DefaultGraphicsContext;
import io.karma.pda.client.session.ClientSessionHandler;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

/**
 * @author Alexander Hinze
 * @since 09/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultDisplayRenderer implements DisplayRenderer {
    public static final DefaultDisplayRenderer INSTANCE = new DefaultDisplayRenderer();

    //private static ShaderInstance BLIT_BW_SHADER;
    //private static final RenderType BLIT_BW_RENDER_TYPE = createBlitRenderType("bw", DisplayRenderer::getBlitBWShader);
    //private static ShaderInstance BLIT_SRGB_SHADER;
    //private static final RenderType BLIT_SRGB_RENDER_TYPE = createBlitRenderType("srgb",
    //    DisplayRenderer::getBlitSRGBShader);
    //private static ShaderInstance BLIT_OLED_SHADER;
    //private static final RenderType BLIT_OLED_RENDER_TYPE = createBlitRenderType("oled",
    //    DisplayRenderer::getBlitOLEDShader);

    private final BufferBuilder blitBuilder = new BufferBuilder(48);
    private final MultiBufferSource.BufferSource blitBufferSource = MultiBufferSource.immediate(blitBuilder);
    private final HashMap<RenderType, BufferBuilder> displayBuilders = new HashMap<>();
    private final BufferBuilder displayBuilder = new BufferBuilder(10000);
    private final MultiBufferSource.BufferSource displayBufferSource = MultiBufferSource.immediateWithBuffers(
        displayBuilders,
        displayBuilder);
    private final PoseStack displayPoseStack = new PoseStack();
    private final DefaultGraphicsContext graphicsContext = new DefaultGraphicsContext();
    private final DefaultGraphics graphics = new DefaultGraphics();
    private final HashMap<DisplayResolution, DefaultFramebuffer> framebuffers = new HashMap<>();
    private final HashMap<DisplayModeSpec, DefaultDisplayMode> displayModes = new HashMap<>();
    private float glitchFactor;

    private DefaultDisplayRenderer() {
        displayPoseStack.setIdentity();
    }

    @Override
    public float getGlitchFactor() {
        return glitchFactor;
    }

    public void setGlitchFactor(final float glitchFactor) {
        this.glitchFactor = glitchFactor;
    }

    //private static RenderType createBlitRenderType(final String name, final Supplier<ShaderInstance> shaderSupplier) {
    //    // @formatter:off
    //    return RenderType.create(String.format("%s:display_blit_%s", Constants.MODID, name),
    //        DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.TRIANGLES, 6, false, false,
    //        RenderType.CompositeState.builder()
    //            .setCullState(RenderStateShard.CULL)
    //            .setTextureState(new RenderStateShard.EmptyTextureStateShard(
    //                () -> {
    //                    RenderSystem.setShaderTexture(0, INSTANCE.getFramebuffer(DisplayResolution.HD_512_576).getTextureId());
    //                    RenderSystem.setShaderTexture(1, PIXEL_TEXTURE);
    //                },
    //                () -> {
    //                    RenderSystem.setShaderTexture(0, 0);
    //                    RenderSystem.setShaderTexture(1, 0);
    //                }
    //            ))
    //            .setShaderState(new RenderStateShard.ShaderStateShard(shaderSupplier))
    //            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
    //            .createCompositeState(false));
    //    // @formatter:on
    //}

    //@SuppressWarnings("all")
    //private static ShaderInstance getBlitBWShader() {
    //    BLIT_BW_SHADER.getUniform("Time").set(ClientEventHandler.INSTANCE.getShaderTime());
    //    BLIT_BW_SHADER.getUniform("GlitchFactor").set(INSTANCE.glitchFactor);
    //    return BLIT_BW_SHADER;
    //}

    //@SuppressWarnings("all")
    //private static ShaderInstance getBlitSRGBShader() {
    //    BLIT_SRGB_SHADER.getUniform("Time").set(ClientEventHandler.INSTANCE.getShaderTime());
    //    BLIT_SRGB_SHADER.getUniform("GlitchFactor").set(INSTANCE.glitchFactor);
    //    return BLIT_SRGB_SHADER;
    //}

    //@SuppressWarnings("all")
    //private static ShaderInstance getBlitOLEDShader() {
    //    BLIT_OLED_SHADER.getUniform("Time").set(ClientEventHandler.INSTANCE.getShaderTime());
    //    BLIT_OLED_SHADER.getUniform("GlitchFactor").set(INSTANCE.glitchFactor);
    //    return BLIT_OLED_SHADER;
    //}

    //private static RenderType getBlitRenderType(final DisplayType type) {
    //    return switch (type) {
    //        case SRGB_LCD -> BLIT_SRGB_RENDER_TYPE;
    //        case OLED -> BLIT_OLED_RENDER_TYPE;
    //        default -> BLIT_BW_RENDER_TYPE;
    //    };
    //}

    @Override
    public DisplayMode getDisplayMode(final ItemStack stack) {
        return null;
    }

    private DefaultDisplayRenderer reset() {
        glitchFactor = 0F;
        return this;
    }

    @ApiStatus.Internal
    public void setupEarly() {
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegisterShaders);
    }

    @ApiStatus.Internal
    public void setup() {
        //framebuffer = new DisplayFramebuffer(RES_X, RES_Y); // Adjust framebuffer size
        //PDAMod.DISPOSITION_HANDLER.addObject(framebuffer);
    }

    //private void onRegisterShaders(final RegisterShadersEvent event) {
    //    try {
    //        PDAMod.LOGGER.debug("Loading display renderer shaders");
    //        event.registerShader(new ShaderInstance(event.getResourceProvider(),
    //            new ResourceLocation(Constants.MODID, "display_blit_bw"),
    //            DefaultVertexFormat.POSITION_TEX_COLOR), shader -> {
    //            BLIT_BW_SHADER = shader;
    //        });
    //        event.registerShader(new ShaderInstance(event.getResourceProvider(),
    //            new ResourceLocation(Constants.MODID, "display_blit_srgb"),
    //            DefaultVertexFormat.POSITION_TEX_COLOR), shader -> {
    //            BLIT_SRGB_SHADER = shader;
    //        });
    //        event.registerShader(new ShaderInstance(event.getResourceProvider(),
    //            new ResourceLocation(Constants.MODID, "display_blit_oled"),
    //            DefaultVertexFormat.POSITION_TEX_COLOR), shader -> {
    //            BLIT_OLED_SHADER = shader;
    //        });
    //    }
    //    catch (Throwable error) {
    //        PDAMod.LOGGER.error("Could not register shader: {}", Exceptions.toFancyString(error));
    //    }
    //}

    @SuppressWarnings("unchecked")
    private void renderIntoDisplayBuffer(final ItemStack stack, final DisplayMode displayMode) {
        final var framebuffer = displayMode.getFramebuffer();

        // Clear display framebuffer
        framebuffer.bind();
        GL11.glClearColor(0F, 0F, 0F, 1F);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        framebuffer.unbind();

        displayPoseStack.pushPose();
        graphicsContext.setup(displayPoseStack,
            displayBufferSource,
            framebuffer.getWidth(),
            framebuffer.getHeight(),
            0,
            displayMode);
        graphics.setContext(graphicsContext);

        final var session = ClientSessionHandler.INSTANCE.findByDevice(stack);

        if (session != null) {
            final var launcher = session.getLauncher();
            final var app = launcher.getCurrentApp();
            if (app != null) {
                final var appRenderer = AppRenderers.get((AppType<App>) app.getType());
                appRenderer.render(app, graphics);
                session.getStateHandler().flush(); // Flush the synchronizer after each frame
            }
        }

        displayBufferSource.endBatch();
        displayPoseStack.popPose();
    }

    @SuppressWarnings("all")
    public void renderDisplay(final ItemStack stack, final MultiBufferSource bufferSource, final PoseStack poseStack) {
        final var displayMode = getDisplayMode(stack);
        renderIntoDisplayBuffer(stack, displayMode);
        final var blitter = displayMode.getBlitter();
        blitter.blit(poseStack.last().pose(), blitBufferSource.getBuffer(blitter.getRenderType()));
        blitBufferSource.endBatch(); // Flush buffer data
    }
}
