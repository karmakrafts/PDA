/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

import io.karma.pda.api.client.render.graphics.Brush;
import io.karma.pda.api.client.render.graphics.BrushFactory;
import io.karma.pda.api.client.render.graphics.Graphics;
import io.karma.pda.api.client.render.graphics.GraphicsContext;
import io.karma.pda.api.common.util.Color;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultGraphics implements Graphics {
    private GraphicsContext context;
    private int zIndex = 0;
    private float lineWidth = 1F;
    private boolean hasTextShadow = false;
    private Brush brush = DefaultBrushFactory.INSTANCE.createInvisible();

    public void setContext(final GraphicsContext context) {
        this.context = context;
    }

    @Override
    public BrushFactory getBrushFactory() {
        return DefaultBrushFactory.INSTANCE;
    }

    @Override
    public GraphicsContext getContext() {
        return context;
    }

    @Override
    public Graphics copy() {
        return copyWithContext(context);
    }

    @Override
    public Graphics copyWithContext(final GraphicsContext context) {
        final var gfx = new DefaultGraphics();
        gfx.setContext(context);
        gfx.zIndex = zIndex;
        gfx.lineWidth = lineWidth;
        gfx.hasTextShadow = hasTextShadow;
        gfx.brush = brush;
        return gfx;
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
    public void setLineWidth(final float lineWidth) {
        this.lineWidth = lineWidth;
    }

    @Override
    public float getLineWidth() {
        return lineWidth;
    }

    @Override
    public void setHasTextShadow(final boolean hasTextShadow) {
        this.hasTextShadow = hasTextShadow;
    }

    @Override
    public boolean hasTextShadow() {
        return hasTextShadow;
    }

    @Override
    public void setBrush(final Brush brush) {
        this.brush = brush;
    }

    @Override
    public Brush getBrush() {
        return brush;
    }

    private void fillRect(final int x, final int y, final int width, final int height, final Color colorTL,
                          final Color colorTR, final Color colorBL, final Color colorBR) {
        final var matrix = context.getTransform();
        final var texture = brush.getTexture();
        final var maxX = x + width;
        final var maxY = y + height;
        if (texture != null) {
            final var buffer = getBuffer();
            // First triangle
            buffer.vertex(matrix, x, y, zIndex).color(colorTL.packARGB()).uv(0F, 0F).endVertex();
            buffer.vertex(matrix, maxX, y, zIndex).color(colorTR.packARGB()).uv(1F, 0F).endVertex();
            buffer.vertex(matrix, x, maxY, zIndex).color(colorBL.packARGB()).uv(0F, 1F).endVertex();
            // Second triangle
            buffer.vertex(matrix, maxX, y, zIndex).color(colorTR.packARGB()).uv(1F, 0F).endVertex();
            buffer.vertex(matrix, maxX, maxY, zIndex).color(colorBR.packARGB()).uv(1F, 1F).endVertex();
            buffer.vertex(matrix, x, maxY, zIndex).color(colorBL.packARGB()).uv(0F, 1F).endVertex();
            return;
        }
        final var buffer = getBuffer();
        // First triangle
        buffer.vertex(matrix, x, y, zIndex).color(colorTL.packARGB()).endVertex();
        buffer.vertex(matrix, maxX, y, zIndex).color(colorTR.packARGB()).endVertex();
        buffer.vertex(matrix, x, maxY, zIndex).color(colorBL.packARGB()).endVertex();
        // Second triangle
        buffer.vertex(matrix, maxX, y, zIndex).color(colorTR.packARGB()).endVertex();
        buffer.vertex(matrix, maxX, maxY, zIndex).color(colorBR.packARGB()).endVertex();
        buffer.vertex(matrix, x, maxY, zIndex).color(colorBL.packARGB()).endVertex();
    }

    @Override
    public void point(final int x, final int y) {
        if (!brush.isVisible()) {
            return;
        }
        final var color = brush.getColor(0);
        fillRect(x, y, 1, 1, color, color, color, color);
    }

    @Override
    public void hLine(final int startX, final int endX, final int y) {
        if (!brush.isVisible()) {
            return;
        }
        final var color0 = brush.getColor(0);
        final var color1 = brush.getColor(1);
        fillRect(startX, y, endX - startX, 1, color0, color1, color0, color1);
    }

    @Override
    public void vLine(final int x, final int startY, final int endY) {
        if (!brush.isVisible()) {
            return;
        }
        final var color0 = brush.getColor(0);
        final var color1 = brush.getColor(1);
        fillRect(x, startY, 1, endY - startY, color0, color0, color1, color1);
    }

    @Override
    public void line(final int startX, final int startY, final int endX, final int endY) {
        if (!brush.isVisible()) {
            return;
        }
        if (startX == endX) {
            vLine(startX, startY, endY);
            return;
        }
        if (startY == endY) {
            hLine(startX, endX, startY);
        }
        // TODO: implement triangle based line rendering
    }

    @Override
    public void drawRect(final int x, final int y, final int width, final int height) {
        if (!brush.isVisible()) {
            return;
        }
        hLine(x, x + width, y);
        hLine(x, x + width, y + height - 1);
        vLine(x, y, y + height);
        vLine(x + width - 1, y, y + height);
    }

    @Override
    public void fillRect(final int x, final int y, final int width, final int height) {
        if (!brush.isVisible()) {
            return;
        }
        fillRect(x, y, width, height, brush.getColor(0), brush.getColor(1), brush.getColor(2), brush.getColor(3));
    }

    @Override
    public void drawCircle(final int x, final int y, final int radius) {
        // TODO: ...
    }

    @Override
    public void fillCircle(final int x, final int y, final int radius) {
        // TODO: ...
    }

    @Override
    public void drawRoundedRect(final int x, final int y, final int width, final int height, final float rounding) {
        // TODO: ...
    }

    @Override
    public void fillRoundedRect(final int x, final int y, final int width, final int height, final float rounding) {
        // TODO: ...
    }

    @Override
    public void drawTriangle(final int x1, final int y1, final int x2, final int y2, final int x3, final int y3) {
        // TODO: ...
    }

    @Override
    public void fillTriangle(final int x1, final int y1, final int x2, final int y2, final int x3, final int y3) {
        // TODO: ...
    }

    @Override
    public void text(final int x, final int y, final String text, final int maxLength, final String delimiter) {
        if (!brush.isVisible()) {
            return;
        }
        context.getFont().drawInBatch(text,
            x,
            y,
            brush.getColor(0).packARGB(),
            hasTextShadow,
            context.getTransform(),
            context.getBufferSource(),
            Font.DisplayMode.NORMAL,
            0,
            OverlayTexture.NO_OVERLAY);
    }

    @Override
    public void wrappedText(final int x, final int y, final String text, final int maxLength) {
        // TODO: ...
    }
}
