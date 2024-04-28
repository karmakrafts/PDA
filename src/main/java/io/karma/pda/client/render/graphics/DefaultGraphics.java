/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

import io.karma.pda.api.client.render.graphics.BrushFactory;
import io.karma.pda.api.client.render.graphics.Graphics;
import io.karma.pda.api.client.render.graphics.GraphicsContext;
import io.karma.pda.api.client.render.graphics.GraphicsState;
import io.karma.pda.api.common.util.Color;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Objects;
import java.util.Stack;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultGraphics implements Graphics {
    private GraphicsContext context;
    private final Stack<GraphicsState> stateStack = new Stack<>();

    public DefaultGraphics() {
        stateStack.push(new DefaultGraphicsState(this));
    }

    public void setContext(final GraphicsContext context) {
        this.context = context;
        Objects.requireNonNull(stateStack.peek()).setZIndex(context.getDefaultZIndex());
    }

    @Override
    public GraphicsState getState() {
        return stateStack.peek();
    }

    @Override
    public GraphicsState pushState() {
        return stateStack.push(new DefaultGraphicsState(this));
    }

    @Override
    public void popState() {
        stateStack.pop();
    }

    @Override
    public void flush() {
        ((MultiBufferSource.BufferSource) context.getBufferSource()).endBatch(getState().getBrush().getRenderType());
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
        return gfx;
    }

    private void fillRect(final int x, final int y, final int width, final int height, final Color colorTL,
                          final Color colorTR, final Color colorBL, final Color colorBR) {
        final var matrix = context.getTransform();
        final var state = getState();
        final var texture = state.getBrush().getTexture();
        final var maxX = x + width;
        final var maxY = y + height;
        final var z = (float) state.getZIndex();
        if (state.shouldForceUVs() || texture != null) {
            final var buffer = getBuffer();
            // First triangle
            buffer.vertex(matrix, x, y, z).uv(0F, 0F).color(colorTL.packARGB()).endVertex();
            buffer.vertex(matrix, maxX, y, z).uv(1F, 0F).color(colorTR.packARGB()).endVertex();
            buffer.vertex(matrix, x, maxY, z).uv(0F, 1F).color(colorBL.packARGB()).endVertex();
            // Second triangle
            buffer.vertex(matrix, maxX, y, z).uv(1F, 0F).color(colorTR.packARGB()).endVertex();
            buffer.vertex(matrix, maxX, maxY, z).uv(1F, 1F).color(colorBR.packARGB()).endVertex();
            buffer.vertex(matrix, x, maxY, z).uv(0F, 1F).color(colorBL.packARGB()).endVertex();
            return;
        }
        final var buffer = getBuffer();
        // First triangle
        buffer.vertex(matrix, x, y, z).color(colorTL.packARGB()).endVertex();
        buffer.vertex(matrix, maxX, y, z).color(colorTR.packARGB()).endVertex();
        buffer.vertex(matrix, x, maxY, z).color(colorBL.packARGB()).endVertex();
        // Second triangle
        buffer.vertex(matrix, maxX, y, z).color(colorTR.packARGB()).endVertex();
        buffer.vertex(matrix, maxX, maxY, z).color(colorBR.packARGB()).endVertex();
        buffer.vertex(matrix, x, maxY, z).color(colorBL.packARGB()).endVertex();
    }

    @Override
    public void point(final int x, final int y) {
        final var brush = getState().getBrush();
        if (!brush.isVisible()) {
            return;
        }
        final var color = brush.getColor(0);
        fillRect(x, y, 1, 1, color, color, color, color);
    }

    @Override
    public void hLine(final int startX, final int endX, final int y) {
        final var brush = getState().getBrush();
        if (!brush.isVisible()) {
            return;
        }
        final var color0 = brush.getColor(0);
        final var color1 = brush.getColor(1);
        fillRect(startX, y, endX - startX, 1, color0, color1, color0, color1);
    }

    @Override
    public void vLine(final int x, final int startY, final int endY) {
        final var brush = getState().getBrush();
        if (!brush.isVisible()) {
            return;
        }
        final var color0 = brush.getColor(0);
        final var color1 = brush.getColor(1);
        fillRect(x, startY, 1, endY - startY, color0, color0, color1, color1);
    }

    @Override
    public void line(final int startX, final int startY, final int endX, final int endY) {
        final var brush = getState().getBrush();
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
        final var brush = getState().getBrush();
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
        final var brush = getState().getBrush();
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
        // TODO: ...
    }

    @Override
    public void wrappedText(final int x, final int y, final String text, final int maxLength) {
        // TODO: ...
    }
}
