/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.gfx;

import io.karma.pda.api.client.render.gfx.Brush;
import io.karma.pda.api.client.render.gfx.BrushFactory;
import io.karma.pda.api.client.render.gfx.GFX;
import io.karma.pda.api.client.render.gfx.GFXContext;
import io.karma.pda.api.common.util.Color;
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
    private Brush brush = DefaultBrushFactory.INSTANCE.create(Color.WHITE);

    public DefaultGFX(final GFXContext context) {
        this.context = context;
    }

    @Override
    public BrushFactory getBrushFactory() {
        return DefaultBrushFactory.INSTANCE;
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

    private void fillRect(final int x, final int y, final int width, final int height, final Color colorTL,
                          final Color colorTR, final Color colorBL, final Color colorBR) {
        final var matrix = context.getTransform();
        final var texture = brush.getTexture();
        final var maxX = x + width;
        final var maxY = y + height;
        if (texture != null) {
            final var buffer = context.getBufferSource().getBuffer(GFXRenderTypes.COLOR_TEXTURE_TRIS);
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
        final var buffer = context.getBufferSource().getBuffer(GFXRenderTypes.COLOR_TRIS);
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
        final var color = brush.getColor(0);
        fillRect(x, y, 1, 1, color, color, color, color);
    }

    @Override
    public void hLine(final int startX, final int endX, final int y) {
        final var color0 = brush.getColor(0);
        final var color1 = brush.getColor(1);
        fillRect(startX, y, endX - startX, 1, color0, color1, color0, color1);
    }

    @Override
    public void vLine(final int x, final int startY, final int endY) {
        final var color0 = brush.getColor(0);
        final var color1 = brush.getColor(1);
        fillRect(x, startY, 1, endY - startY, color0, color0, color1, color1);
    }

    @Override
    public void line(final int startX, final int startY, final int endX, final int endY) {
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
        hLine(x, x + width, y);
        hLine(x, x + width, y + height - 1);
        vLine(x, y, y + height);
        vLine(x + width - 1, y, y + height);
    }

    @Override
    public void fillRect(final int x, final int y, final int width, final int height) {
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
        // TODO: ...
    }

    @Override
    public void wrappedText(final int x, final int y, final String text, final int maxLength) {
        // TODO: ...
    }
}
