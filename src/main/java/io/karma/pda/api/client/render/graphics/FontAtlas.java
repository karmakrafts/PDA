/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.graphics;

import io.karma.pda.api.common.app.theme.font.Font;
import io.karma.pda.api.common.dispose.Disposable;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.CharSet;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 04/05/2024
 */
@OnlyIn(Dist.CLIENT)
public interface FontAtlas extends Disposable {
    Font getFont();

    GlyphSprite getGlyphSprite(final char c);

    CharSet getSupportedChars();

    Char2ObjectMap<GlyphSprite> getGlyphSprites();

    int getWidth();

    int getHeight();

    int getSpriteSize();

    int getSpriteBorder();

    float getMaxGlyphWidth();

    float getMaxGlyphHeight();

    float getMaxGlyphBearingX();

    float getMaxGlyphBearingY();

    float getLineHeight();

    int getTextureId();

    void bind();

    void unbind();

    boolean isReady();

    float getUScale();

    float getVScale();

    float getSDFRange();
}
