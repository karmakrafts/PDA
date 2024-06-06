/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

import com.mojang.blaze3d.vertex.PoseStack;
import io.karma.pda.PDAMod;
import io.karma.pda.api.client.render.display.DisplayMode;
import io.karma.pda.api.client.render.graphics.BrushFactory;
import io.karma.pda.api.client.render.graphics.FontRenderer;
import io.karma.pda.api.client.render.graphics.Graphics;
import io.karma.pda.api.client.render.graphics.GraphicsContext;
import io.karma.pda.client.render.graphics.font.DefaultFontRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultGraphicsContext implements GraphicsContext {
    private final DefaultBrushFactory brushFactory = new DefaultBrushFactory(this);
    private Graphics graphics;
    private PoseStack poseStack;
    private MultiBufferSource bufferSource;
    private int width;
    private int height;
    private int defaultZIndex;
    private DisplayMode displayMode;

    public void setup(final PoseStack poseStack, final MultiBufferSource bufferSource, final int width,
                      final int height, final int defaultZIndex, final DisplayMode displayMode) {
        this.poseStack = poseStack;
        this.bufferSource = bufferSource;
        this.width = width;
        this.height = height;
        this.defaultZIndex = defaultZIndex;
        this.displayMode = displayMode;
    }

    @Override
    public Graphics getGraphics() {
        return graphics;
    }

    public void setGraphics(final Graphics graphics) {
        this.graphics = graphics;
    }

    @Override
    public DisplayMode getDisplayMode() {
        return displayMode;
    }

    @Override
    public BrushFactory getBrushFactory() {
        return brushFactory;
    }

    @Override
    public FontRenderer getFontRenderer() {
        return DefaultFontRenderer.INSTANCE;
    }

    @Override
    public int getDefaultZIndex() {
        return defaultZIndex;
    }

    @Override
    public GraphicsContext derive(final int width, final int height) {
        final var context = new DefaultGraphicsContext();
        context.setup(poseStack, bufferSource, width, height, defaultZIndex + 1, context.displayMode);
        return context;
    }

    @Override
    public PoseStack getPoseStack() {
        return poseStack;
    }

    @Override
    public MultiBufferSource getBufferSource() {
        return bufferSource;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public boolean isDebugMode() {
        return PDAMod.IS_DEV_ENV;
    }
}
