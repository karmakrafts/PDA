/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.display;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import io.karma.pda.api.app.App;
import io.karma.pda.api.app.AppType;
import io.karma.pda.api.client.render.app.AppRenderers;
import io.karma.pda.api.client.render.display.DisplayMode;
import io.karma.pda.api.client.render.display.DisplayRenderer;
import io.karma.pda.api.display.DisplayModeSpec;
import io.karma.pda.api.display.DisplayResolution;
import io.karma.pda.api.util.LogMarkers;
import io.karma.pda.mod.PDAMod;
import io.karma.pda.mod.client.render.graphics.DefaultBrushFactory;
import io.karma.pda.mod.client.render.graphics.DefaultGraphics;
import io.karma.pda.mod.client.render.graphics.DefaultGraphicsContext;
import io.karma.pda.mod.client.session.ClientSessionHandler;
import io.karma.pda.mod.item.PDAItem;
import io.karma.peregrine.api.font.FontRenderer;
import io.karma.peregrine.api.framebuffer.AttachmentType;
import io.karma.peregrine.api.framebuffer.Framebuffer;
import io.karma.peregrine.api.texture.DefaultTextureFormat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.HashMap;
import java.util.Optional;

/**
 * @author Alexander Hinze
 * @since 09/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultDisplayRenderer implements DisplayRenderer {
    public static final DefaultDisplayRenderer INSTANCE = new DefaultDisplayRenderer();

    private final HashMap<RenderType, BufferBuilder> displayBuilders = new HashMap<>();
    private final BufferBuilder displayBuilder = new BufferBuilder(100000);
    private final MultiBufferSource.BufferSource displayBufferSource = MultiBufferSource.immediateWithBuffers(
        displayBuilders,
        displayBuilder);
    private final PoseStack displayPoseStack = new PoseStack();
    private final HashMap<DisplayResolution, Framebuffer> framebuffers = new HashMap<>();
    private final HashMap<DisplayModeSpec, DefaultDisplayMode> displayModes = new HashMap<>();
    private final DefaultGraphicsContext graphicsContext = new DefaultGraphicsContext();
    private final DefaultGraphics graphics = new DefaultGraphics();
    private final DefaultBrushFactory brushFactory = new DefaultBrushFactory(graphicsContext);
    private final HashMap<DisplayResolution, FontRenderer> fontRenderers = new HashMap<>();
    private float glitchFactor;

    private DefaultDisplayRenderer() {
        displayPoseStack.setIdentity();
    }

    @Internal
    public void createDisplayMode(final DisplayModeSpec modeSpec) {
        if (displayModes.containsKey(modeSpec)) {
            return;
        }
        PDAMod.LOGGER.debug(LogMarkers.RENDERER, "Creating display mode {}", modeSpec);
        displayModes.put(modeSpec,
            new DefaultDisplayMode(modeSpec, getFramebuffer(modeSpec.resolution()), this::getGlitchFactor));
    }

    @Override
    public DefaultGraphics getGraphics() {
        return graphics;
    }

    @Override
    public FontRenderer getFontRenderer(final DisplayResolution resolution) {
        return fontRenderers.computeIfAbsent(resolution, res -> FontRenderer.create(getFramebuffer(res)));
    }

    @Override
    public DefaultBrushFactory getBrushFactory() {
        return brushFactory;
    }

    @Override
    public float getGlitchFactor() {
        return glitchFactor;
    }

    public void setGlitchFactor(final float glitchFactor) {
        this.glitchFactor = glitchFactor;
    }

    @Override
    public DefaultDisplayMode getDisplayMode(final DisplayModeSpec modeSpec) {
        return displayModes.get(modeSpec);
    }

    @Override
    public Optional<DisplayMode> getDisplayMode(final ItemStack stack) {
        return PDAItem.getDisplayMode(stack).map(this::getDisplayMode);
    }

    @Override
    public Framebuffer getFramebuffer(final DisplayResolution resolution) {
        // @formatter:off
        return framebuffers.computeIfAbsent(resolution, res -> Framebuffer.create(it -> it
            .width(res.getWidth())
            .height(res.getHeight())
            .attachment(it2 -> it2
                .type(AttachmentType.COLOR)
                .format(DefaultTextureFormat.RGBA32F)
            )
            .attachment(it2 -> it2
                .type(AttachmentType.DEPTH)
                .format(DefaultTextureFormat.DEPTH_32)
            )
        ));
        // @formatter:on
    }

    @SuppressWarnings("unchecked")
    private void renderIntoDisplayBuffer(final ItemStack stack, final DisplayMode displayMode) {
        final var framebuffer = displayMode.getFramebuffer();

        framebuffer.bind();
        framebuffer.clear(0F, 0F, 0F, 1F);
        framebuffer.unbind();

        displayPoseStack.pushPose();
        graphicsContext.setup(displayPoseStack,
            displayBufferSource,
            framebuffer.getWidth(),
            framebuffer.getHeight(),
            0,
            displayMode,
            brushFactory,
            getFontRenderer(displayMode.getResolution()));
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
    public void renderDisplay(final ItemStack stack,
                              final MultiBufferSource bufferSource,
                              final PoseStack poseStack,
                              final int packedLight,
                              final int packedOverlay) {
        getDisplayMode(stack).ifPresent(displayMode -> {
            renderIntoDisplayBuffer(stack, displayMode);
            final var blitter = displayMode.getBlitter();
            blitter.blit(poseStack.last().pose(),
                bufferSource.getBuffer(blitter.getRenderType()),
                packedLight,
                packedOverlay);
            //blitBufferSource.endBatch();
        });
    }
}
