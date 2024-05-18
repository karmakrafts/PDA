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
    private final int advance;
    private final int bearingX;
    private final int bearingY;

    public DefaultGlyphMetrics(final int width, final int height, final int ascent, final int descent,
                               final int advance, final int bearingX, final int bearingY) {
        this.width = width;
        this.height = height;
        this.ascent = ascent;
        this.descent = descent;
        this.advance = advance;
        this.bearingX = bearingX;
        this.bearingY = bearingY;
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
    public int getAdvance() {
        return advance;
    }

    @Override
    public int getBearingX() {
        return bearingX;
    }

    @Override
    public int getBearingY() {
        return bearingY;
    }

    @Override
    public String toString() {
        return String.format("[ASC%d,DES%d,ADV%d,BX%d,BY%d]", ascent, descent, advance, bearingX, bearingY);
    }
}
