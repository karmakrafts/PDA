/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.graphics;

import io.karma.pda.api.common.app.theme.font.Font;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 04/05/2024
 */
@OnlyIn(Dist.CLIENT)
public interface FontAtlas {
    Font getFont();

    GlyphSprite getGlyphSprite(final char c);

    Char2ObjectMap<GlyphSprite> getGlyphSprites();

    void bind();

    void unbind();
}
