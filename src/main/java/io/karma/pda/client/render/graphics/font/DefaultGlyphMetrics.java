/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics.font;

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
    private final int advanceX;
    private final int advanceY;
    private final int bearingX;
    private final int bearingY;

    public DefaultGlyphMetrics(final int width, final int height, final int ascent, final int descent,
                               final int advanceX, final int advanceY, final int bearingX, final int bearingY) {
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
    public int getAdvanceX() {
        return advanceX;
    }

    @Override
    public int getAdvanceY() {
        return advanceY;
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
        return String.format("[ASC%f,DES%f,ADV%f,BX%f,BY%f]", ascent, descent, advanceX, bearingX, bearingY);
    }
}
