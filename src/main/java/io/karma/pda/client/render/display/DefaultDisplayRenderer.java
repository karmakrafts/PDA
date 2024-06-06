/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.display;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import io.karma.pda.api.client.render.app.AppRenderers;
import io.karma.pda.api.client.render.display.DisplayMode;
import io.karma.pda.api.client.render.display.DisplayRenderer;
import io.karma.pda.api.client.render.display.Framebuffer;
import io.karma.pda.api.common.app.App;
import io.karma.pda.api.common.app.AppType;
import io.karma.pda.api.common.display.DisplayModeSpec;
import io.karma.pda.api.common.display.DisplayResolution;
import io.karma.pda.client.render.graphics.DefaultGraphics;
import io.karma.pda.client.render.graphics.DefaultGraphicsContext;
import io.karma.pda.client.session.ClientSessionHandler;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.item.PDAItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

/**
 * @author Alexander Hinze
 * @since 09/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultDisplayRenderer implements DisplayRenderer {
    public static final DefaultDisplayRenderer INSTANCE = new DefaultDisplayRenderer();

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

    @ApiStatus.Internal
    public void createDisplayModes(final Collection<DisplayModeSpec> modeSpecs) {
        for (final var modeSpec : modeSpecs) {
            PDAMod.LOGGER.debug("Creating display mode {}", modeSpec);
            displayModes.put(modeSpec, new DefaultDisplayMode(modeSpec, getFramebuffer(modeSpec.resolution())));
        }
    }

    @Override
    public float getGlitchFactor() {
        return glitchFactor;
    }

    public void setGlitchFactor(final float glitchFactor) {
        this.glitchFactor = glitchFactor;
    }

    @Override
    public DisplayMode getDisplayMode(final DisplayModeSpec modeSpec) {
        return displayModes.get(modeSpec);
    }

    @Override
    public Optional<DisplayMode> getDisplayMode(final ItemStack stack) {
        return PDAItem.getDisplayMode(stack).map(displayModes::get);
    }

    @Override
    public Framebuffer getFramebuffer(final DisplayResolution resolution) {
        return framebuffers.computeIfAbsent(resolution, DefaultFramebuffer::new);
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
        getDisplayMode(stack).ifPresent(displayMode -> {
            renderIntoDisplayBuffer(stack, displayMode);
            final var blitter = displayMode.getBlitter();
            blitter.blit(poseStack.last().pose(), blitBufferSource.getBuffer(blitter.getRenderType()));
            blitBufferSource.endBatch(); // Flush buffer data
        });
    }
}
