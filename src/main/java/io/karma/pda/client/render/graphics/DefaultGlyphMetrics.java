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
    private final int width;
    private final int height;
    private final int ascent;
    private final int descent;
    private final int lineGap;
    private final int advance;
    private final int leftSideBearing;

    public DefaultGlyphMetrics(final int width, final int height, final int ascent, final int descent,
                               final int lineGap, final int advance, final int leftSideBearing) {
        this.width = width;
        this.height = height;
        this.ascent = ascent;
        this.descent = descent;
        this.lineGap = lineGap;
        this.advance = advance;
        this.leftSideBearing = leftSideBearing;
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
    public int getAscent() {
        return ascent;
    }

    @Override
    public int getDescent() {
        return descent;
    }

    @Override
    public int getLineGap() {
        return lineGap;
    }

    @Override
    public int getAdvance() {
        return advance;
    }

    @Override
    public int getLeftSideBearing() {
        return leftSideBearing;
    }

    @Override
    public String toString() {
        return String.format("[ASC%d,DES%d,GAP%d,ADV%d,LSB%d]", ascent, descent, lineGap, advance, leftSideBearing);
    }
}
