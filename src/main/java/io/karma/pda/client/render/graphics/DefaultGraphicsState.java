/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

import io.karma.pda.api.client.render.graphics.Brush;
import io.karma.pda.api.client.render.graphics.Graphics;
import io.karma.pda.api.client.render.graphics.GraphicsState;
import io.karma.pda.api.common.app.theme.font.FontFamily;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 28/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultGraphicsState implements GraphicsState {
    private final Graphics graphics;
    private Brush brush;
    private FontFamily fontFamily;
    private float lineWidth;
    private boolean hasTextShadows;
    private int zIndex;
    private boolean forceUVs;

    DefaultGraphicsState(final Graphics graphics) {
        this.graphics = graphics;
    }

    @Override
    public void setFontFamily(final FontFamily fontFamily) {
        this.fontFamily = fontFamily;
    }

    @Override
    public FontFamily getFontFamily() {
        return fontFamily;
    }

    @Override
    public void setBrush(final Brush brush) {
        this.brush = brush;
    }

    @Override
    public void setLineWidth(final float lineWidth) {
        this.lineWidth = lineWidth;
    }

    @Override
    public void setHasTextShadows(final boolean hasTextShadows) {
        this.hasTextShadows = hasTextShadows;
    }

    @Override
    public void setZIndex(final int zIndex) {
        this.zIndex = zIndex;
    }

    @Override
    public void setForceUVs(final boolean forceUVs) {
        this.forceUVs = forceUVs;
    }

    @Override
    public Brush getBrush() {
        return brush;
    }

    @Override
    public float getLineWidth() {
        return lineWidth;
    }

    @Override
    public boolean hasTextShadows() {
        return hasTextShadows;
    }

    @Override
    public int getZIndex() {
        return zIndex;
    }

    @Override
    public boolean shouldForceUVs() {
        return forceUVs;
    }

    @Override
    public void close() {
        graphics.popState();
    }
}
