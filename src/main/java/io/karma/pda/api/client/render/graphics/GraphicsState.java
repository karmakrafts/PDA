/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.graphics;

import io.karma.pda.api.common.app.theme.font.Font;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 28/04/2024
 */
@OnlyIn(Dist.CLIENT)
public interface GraphicsState extends AutoCloseable {
    void setBrush(final Brush brush);

    void setFont(final Font font);

    void setLineWidth(final float lineWidth);

    void setHasTextShadows(final boolean hasTextShadows);

    void setZIndex(final int zIndex);

    void setForceUVs(final boolean forceUVs);

    void setFlipLineColors(final boolean flipLineColors);

    Brush getBrush();

    Font getFont();

    float getLineWidth();

    boolean hasTextShadows();

    int getZIndex();

    boolean shouldForceUVs();

    boolean shouldFlipLineColors();

    @Override
    void close();
}
