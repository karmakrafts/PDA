/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.graphics;

import io.karma.pda.api.common.app.theme.font.FontFamily;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 28/04/2024
 */
@OnlyIn(Dist.CLIENT)
public interface GraphicsState extends AutoCloseable {
    void setBrush(final Brush brush);

    void setFontFamily(final FontFamily fontFamily);

    void setLineWidth(final float lineWidth);

    void setHasTextShadows(final boolean hasTextShadows);

    void setZIndex(final int zIndex);

    void setForceUVs(final boolean forceUVs);

    Brush getBrush();

    FontFamily getFontFamily();

    float getLineWidth();

    boolean hasTextShadows();

    int getZIndex();

    boolean shouldForceUVs();

    @Override
    void close();
}
