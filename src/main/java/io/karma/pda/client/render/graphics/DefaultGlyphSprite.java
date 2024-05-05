/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

import io.karma.pda.api.client.render.graphics.GlyphSprite;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 04/05/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultGlyphSprite implements GlyphSprite {
    private final int width;
    private final int height;
    private final float minU;
    private final float minV;
    private final float maxU;
    private final float maxV;

    public DefaultGlyphSprite(final int width, final int height, final float minU, final float minV, final float maxU,
                              final float maxV) {
        this.width = width;
        this.height = height;
        this.minU = minU;
        this.minV = minV;
        this.maxU = maxU;
        this.maxV = maxV;
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
    public float getMinU() {
        return minU;
    }

    @Override
    public float getMinV() {
        return minV;
    }

    @Override
    public float getMaxU() {
        return maxU;
    }

    @Override
    public float getMaxV() {
        return maxV;
    }
}
