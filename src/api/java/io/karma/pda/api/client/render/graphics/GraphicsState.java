/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.graphics;

import io.karma.peregrine.api.font.Font;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 28/04/2024
 */
@OnlyIn(Dist.CLIENT)
public interface GraphicsState extends AutoCloseable {
    void setHasTextShadows(final boolean hasTextShadows);

    void setForceUVs(final boolean forceUVs);

    void setFlipLineColors(final boolean flipLineColors);

    Brush getBrush();

    void setBrush(final Brush brush);

    Font getFont();

    void setFont(final Font font);

    float getLineWidth();

    void setLineWidth(final float lineWidth);

    boolean hasTextShadows();

    int getZIndex();

    void setZIndex(final int zIndex);

    boolean shouldForceUVs();

    boolean shouldFlipLineColors();

    @Override
    void close();
}
