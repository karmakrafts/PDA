/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.graphics;

import io.karma.pda.api.client.render.graphics.Brush;
import io.karma.pda.api.client.render.graphics.Graphics;
import io.karma.pda.api.client.render.graphics.GraphicsState;
import io.karma.peregrine.api.font.Font;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 28/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultGraphicsState implements GraphicsState {
    private final Graphics graphics;
    private Brush brush = InvisibleBrush.INSTANCE;
    private Font font;
    private float lineWidth = 1F;
    private boolean hasTextShadows;
    private int zIndex = 0;
    private boolean forceUVs;
    private boolean flipLineColors;

    DefaultGraphicsState(final Graphics graphics) {
        this.graphics = graphics;
    }

    @Override
    public void setFlipLineColors(final boolean flipLineColors) {
        this.flipLineColors = flipLineColors;
    }

    @Override
    public boolean shouldFlipLineColors() {
        return flipLineColors;
    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public void setFont(final Font font) {
        this.font = font;
    }

    @Override
    public void setHasTextShadows(final boolean hasTextShadows) {
        this.hasTextShadows = hasTextShadows;
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
    public void setBrush(final Brush brush) {
        this.brush = brush;
    }

    @Override
    public float getLineWidth() {
        return lineWidth;
    }

    @Override
    public void setLineWidth(final float lineWidth) {
        this.lineWidth = lineWidth;
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
    public void setZIndex(final int zIndex) {
        this.zIndex = zIndex;
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
