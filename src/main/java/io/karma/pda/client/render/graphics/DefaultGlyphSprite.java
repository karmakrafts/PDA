/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

import io.karma.pda.api.client.render.graphics.GlyphMetrics;
import io.karma.pda.api.client.render.graphics.GlyphSprite;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 04/05/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultGlyphSprite implements GlyphSprite {
    private final GlyphMetrics metrics;
    private final int width;
    private final int height;
    private final float u;
    private final float v;

    public DefaultGlyphSprite(final GlyphMetrics metrics, final int width, final int height, final float u,
                              final float v) {
        this.metrics = metrics;
        this.width = width;
        this.height = height;
        this.u = u;
        this.v = v;
    }

    @Override
    public GlyphMetrics getMetrics() {
        return metrics;
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
    public float getU() {
        return u;
    }

    @Override
    public float getV() {
        return v;
    }
}
