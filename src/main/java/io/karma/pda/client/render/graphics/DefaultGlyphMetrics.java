/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

import io.karma.pda.api.client.render.graphics.GlyphMetrics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 10/05/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultGlyphMetrics implements GlyphMetrics {
    private final float width;
    private final float height;
    private final float ascent;
    private final float descent;
    private final float advanceX;
    private final float advanceY;
    private final float bearingX;
    private final float bearingY;

    public DefaultGlyphMetrics(final float width, final float height, final float ascent, final float descent,
                               final float advanceX, final float advanceY, final float bearingX, final float bearingY) {
        this.width = width;
        this.height = height;
        this.ascent = ascent;
        this.descent = descent;
        this.advanceX = advanceX;
        this.advanceY = advanceY;
        this.bearingX = bearingX;
        this.bearingY = bearingY;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public float getAscent() {
        return ascent;
    }

    @Override
    public float getDescent() {
        return descent;
    }

    @Override
    public float getAdvanceX() {
        return advanceX;
    }

    @Override
    public float getAdvanceY() {
        return advanceY;
    }

    @Override
    public float getBearingX() {
        return bearingX;
    }

    @Override
    public float getBearingY() {
        return bearingY;
    }

    @Override
    public String toString() {
        return String.format("[ASC%f,DES%f,ADV%f,BX%f,BY%f]", ascent, descent, advanceX, bearingX, bearingY);
    }
}
