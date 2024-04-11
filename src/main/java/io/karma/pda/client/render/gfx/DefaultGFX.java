/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.gfx;

import com.mojang.blaze3d.vertex.PoseStack;
import io.karma.pda.api.client.render.gfx.Brush;
import io.karma.pda.api.client.render.gfx.GFX;
import io.karma.pda.api.client.render.gfx.GFXContext;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultGFX implements GFX {
    private final GFXContext context;
    private int zIndex = 0;
    private Brush brush;

    public DefaultGFX(final GFXContext context) {
        this.context = context;
    }

    public static DefaultGFX create(final PoseStack poseStack, final MultiBufferSource bufferSource, final int width,
                                    final int height) {
        return new DefaultGFX(new DefaultGFXContext(poseStack, bufferSource, width, height));
    }

    @Override
    public GFXContext getContext() {
        return context;
    }

    @Override
    public GFX copy() {
        return new DefaultGFX(context);
    }

    @Override
    public void setZIndex(final int index) {
        zIndex = index;
    }

    @Override
    public int getZIndex() {
        return zIndex;
    }

    @Override
    public void setBrush(final Brush brush) {
        this.brush = brush;
    }

    @Override
    public Brush getBrush() {
        return brush;
    }

    @Override
    public void point(final int x, final int y) {

    }

    @Override
    public void line(final int startX, final int startY, final int endX, final int endY) {

    }

    @Override
    public void drawRect(final int x, final int y, final int width, final int height) {

    }

    @Override
    public void fillRect(final int x, final int y, final int width, final int height) {

    }

    @Override
    public void drawCircle(final int x, final int y, final int radius) {

    }

    @Override
    public void fillCircle(final int x, final int y, final int radius) {

    }

    @Override
    public void drawRoundedRect(final int x, final int y, final int width, final int height, final float rounding) {

    }

    @Override
    public void fillRoundedRect(final int x, final int y, final int width, final int height, final float rounding) {

    }

    @Override
    public void drawTriangle(final int x1, final int y1, final int x2, final int y2, final int x3, final int y3) {

    }

    @Override
    public void fillTriangle(final int x1, final int y1, final int x2, final int y2, final int x3, final int y3) {

    }

    @Override
    public void text(final int x, final int y, final String text, final int maxLength, final String delimiter) {

    }

    @Override
    public void wrappedText(final int x, final int y, final String text, final int maxLength) {

    }
}
