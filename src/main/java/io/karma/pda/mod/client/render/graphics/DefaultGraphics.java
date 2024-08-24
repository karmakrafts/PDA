/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.graphics;

import io.karma.pda.api.client.render.graphics.FontRenderer;
import io.karma.pda.api.client.render.graphics.Graphics;
import io.karma.pda.api.client.render.graphics.GraphicsContext;
import io.karma.pda.api.client.render.graphics.GraphicsState;
import io.karma.pda.api.color.ColorProvider;
import io.karma.pda.api.util.RectangleCorner;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Objects;
import java.util.Stack;
import java.util.function.IntFunction;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultGraphics implements Graphics {
    private final Stack<GraphicsState> stateStack = new Stack<>();
    private GraphicsContext context;
    private FontRenderer fontRenderer;

    public DefaultGraphics() {
        stateStack.push(new DefaultGraphicsState(this));
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
        ((MultiBufferSource.BufferSource) context.getBufferSource()).endBatch(getState().getBrush().getRenderType(
            context.getDisplayMode()));
    }

    @Override
    public GraphicsContext getContext() {
        return context;
    }

    public void setContext(final GraphicsContext context) {
        this.context = context;
        fontRenderer = context.getFontRenderer();
        Objects.requireNonNull(stateStack.peek()).setZIndex(context.getDefaultZIndex());
    }

    @Override
    public Graphics copy() {
        return copyWithContext(context);
    }

    @Override
    public Graphics copyWithContext(final GraphicsContext context) {
        final var graphics = new DefaultGraphics();
        graphics.setContext(context);
        return graphics;
    }

    private void fillRect(final int x,
                          final int y,
                          final int width,
                          final int height,
                          final int colorTL,
                          final int colorTR,
                          final int colorBL,
                          final int colorBR) {
        final var matrix = context.getTransform();
        final var state = getState();
        final var texture = state.getBrush().getTexture();
        final var maxX = x + width;
        final var maxY = y + height;
        final var z = (float) state.getZIndex();
        if (state.shouldForceUVs() || texture != null) {
            final var buffer = getBuffer();
            // First triangle
            buffer.vertex(matrix, x, y, z).uv(0F, 0F).color(colorTL).endVertex();
            buffer.vertex(matrix, maxX, y, z).uv(1F, 0F).color(colorTR).endVertex();
            buffer.vertex(matrix, x, maxY, z).uv(0F, 1F).color(colorBL).endVertex();
            // Second triangle
            buffer.vertex(matrix, maxX, y, z).uv(1F, 0F).color(colorTR).endVertex();
            buffer.vertex(matrix, maxX, maxY, z).uv(1F, 1F).color(colorBR).endVertex();
            buffer.vertex(matrix, x, maxY, z).uv(0F, 1F).color(colorBL).endVertex();
            return;
        }
        final var buffer = getBuffer();
        // First triangle
        buffer.vertex(matrix, x, y, z).color(colorTL).endVertex();
        buffer.vertex(matrix, maxX, y, z).color(colorTR).endVertex();
        buffer.vertex(matrix, x, maxY, z).color(colorBL).endVertex();
        // Second triangle
        buffer.vertex(matrix, maxX, y, z).color(colorTR).endVertex();
        buffer.vertex(matrix, maxX, maxY, z).color(colorBR).endVertex();
        buffer.vertex(matrix, x, maxY, z).color(colorBL).endVertex();
    }

    @Override
    public void point(final int x, final int y) {
        final var state = getState();
        final var brush = state.getBrush();
        if (!brush.isVisible()) {
            return;
        }
        final var color = brush.getColor(state.shouldFlipLineColors() ? RectangleCorner.TOP_RIGHT : RectangleCorner.TOP_LEFT);
        fillRect(x, y, 1, 1, color, color, color, color);
    }

    @Override
    public void hLine(final int startX, final int endX, final int y) {
        final var state = getState();
        final var brush = state.getBrush();
        if (!brush.isVisible()) {
            return;
        }
        final var flipColors = state.shouldFlipLineColors();
        final var color0 = brush.getColor(flipColors ? RectangleCorner.BOTTOM_LEFT : RectangleCorner.TOP_LEFT);
        final var color1 = brush.getColor(flipColors ? RectangleCorner.BOTTOM_RIGHT : RectangleCorner.TOP_RIGHT);
        fillRect(startX, y, endX - startX, 1, color0, color1, color0, color1);
    }

    @Override
    public void vLine(final int x, final int startY, final int endY) {
        final var state = getState();
        final var brush = state.getBrush();
        if (!brush.isVisible()) {
            return;
        }
        final var flipColors = state.shouldFlipLineColors();
        final var color0 = brush.getColor(flipColors ? RectangleCorner.TOP_RIGHT : RectangleCorner.TOP_LEFT);
        final var color1 = brush.getColor(flipColors ? RectangleCorner.BOTTOM_RIGHT : RectangleCorner.BOTTOM_LEFT);
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
        final var state = getState();
        final var brush = state.getBrush();
        if (!brush.isVisible()) {
            return;
        }
        hLine(x, x + width, y);
        state.setFlipLineColors(true);
        hLine(x, x + width, y + height - 1);
        state.setFlipLineColors(false);
        vLine(x, y, y + height);
        state.setFlipLineColors(true);
        vLine(x + width - 1, y, y + height);
        state.setFlipLineColors(false);
    }

    @Override
    public void fillRect(final int x, final int y, final int width, final int height) {
        final var brush = getState().getBrush();
        if (!brush.isVisible()) {
            return;
        }
        // @formatter:off
        fillRect(x, y, width, height, brush.getColor(RectangleCorner.TOP_LEFT),
            brush.getColor(RectangleCorner.TOP_RIGHT),
            brush.getColor(RectangleCorner.BOTTOM_LEFT),
            brush.getColor(RectangleCorner.BOTTOM_RIGHT));
        // @formatter:on
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
    public void drawTriangle(final int x1, final int y1, final int x2, final int y2, final int x3, final int y3) {
        final var brush = getState().getBrush();
        if (!brush.isVisible()) {
            return;
        }
        line(x1, y1, x2, y2);
        line(x2, y2, x3, y3);
        line(x3, y3, x1, y1);
    }

    @Override
    public void fillTriangle(final int x1, final int y1, final int x2, final int y2, final int x3, final int y3) {
        final var state = getState();
        final var brush = state.getBrush();
        if (!brush.isVisible()) {
            return;
        }
        final var buffer = getBuffer();
        final var z = (float) state.getZIndex();
        final var matrix = context.getTransform();
        if (state.shouldForceUVs() || brush.getTexture() != null) {
            buffer.vertex(matrix, x1, y1, z).uv(0F, 0F).color(brush.getColor(RectangleCorner.TOP_LEFT)).endVertex();
            buffer.vertex(matrix, x2, y2, z).uv(1F, 0F).color(brush.getColor(RectangleCorner.BOTTOM_LEFT)).endVertex();
            buffer.vertex(matrix, x3, y3, z).uv(0F, 1F).color(brush.getColor(RectangleCorner.TOP_RIGHT)).endVertex();
            return;
        }
        buffer.vertex(matrix, x1, y1, z).color(brush.getColor(RectangleCorner.TOP_LEFT)).endVertex();
        buffer.vertex(matrix, x2, y2, z).color(brush.getColor(RectangleCorner.BOTTOM_LEFT)).endVertex();
        buffer.vertex(matrix, x3, y3, z).color(brush.getColor(RectangleCorner.TOP_RIGHT)).endVertex();
    }

    @Override
    public int text(final int x, final int y, final char c) {
        final var state = getState();
        return fontRenderer.render(x, y, c, state.getFont(), state.getBrush());
    }

    @Override
    public int text(final int x, final int y, final CharSequence text) {
        final var state = getState();
        return fontRenderer.render(x, y, text, state.getFont(), state.getBrush());
    }

    @Override
    public int text(final int x, final int y, final int maxWidth, final CharSequence text) {
        final var state = getState();
        return fontRenderer.render(x, y, maxWidth, text, state.getFont(), state.getBrush());
    }

    @Override
    public int text(final int x, final int y, final int maxWidth, final int maxHeight, final CharSequence text) {
        final var state = getState();
        return fontRenderer.render(x, y, maxWidth, maxHeight, text, state.getFont(), state.getBrush());
    }

    @Override
    public int text(final int x,
                    final int y,
                    final int maxWidth,
                    final CharSequence text,
                    final CharSequence cutoffSuffix) {
        final var state = getState();
        return fontRenderer.render(x, y, maxWidth, text, cutoffSuffix, state.getFont(), state.getBrush());
    }

    @Override
    public int text(final int x,
                    final int y,
                    final int maxWidth,
                    final int maxHeight,
                    final CharSequence text,
                    final CharSequence cutoffSuffix) {
        final var state = getState();
        return fontRenderer.render(x, y, maxWidth, maxHeight, text, cutoffSuffix, state.getFont(), state.getBrush());
    }

    @Override
    public int text(final int x, final int y, final CharSequence text, final IntFunction<ColorProvider> color) {
        final var state = getState();
        return fontRenderer.render(x, y, text, state.getFont(), color);
    }

    @Override
    public int text(final int x,
                    final int y,
                    final int maxWidth,
                    final CharSequence text,
                    final IntFunction<ColorProvider> color) {
        final var state = getState();
        return fontRenderer.render(x, y, maxWidth, text, state.getFont(), color);
    }

    @Override
    public int text(final int x,
                    final int y,
                    final int maxWidth,
                    final int maxHeight,
                    final CharSequence text,
                    final IntFunction<ColorProvider> color) {
        final var state = getState();
        return fontRenderer.render(x, y, maxWidth, maxHeight, text, state.getFont(), color);
    }

    @Override
    public int text(final int x,
                    final int y,
                    final int maxWidth,
                    final CharSequence text,
                    final CharSequence cutoffSuffix,
                    final IntFunction<ColorProvider> color) {
        final var state = getState();
        return fontRenderer.render(x, y, maxWidth, text, cutoffSuffix, state.getFont(), color);
    }

    @Override
    public int text(final int x,
                    final int y,
                    final int maxWidth,
                    final int maxHeight,
                    final CharSequence text,
                    final CharSequence cutoffSuffix,
                    final IntFunction<ColorProvider> color) {
        final var state = getState();
        return fontRenderer.render(x, y, maxWidth, maxHeight, text, cutoffSuffix, state.getFont(), color);
    }
}
