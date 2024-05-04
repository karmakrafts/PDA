/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

import io.karma.pda.api.client.render.graphics.FontAtlas;
import io.karma.pda.api.client.render.graphics.GlyphSprite;
import io.karma.pda.api.common.app.theme.font.Font;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMaps;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 04/05/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultFontAtlas implements FontAtlas {
    private final Font font;
    private final Char2ObjectOpenHashMap<GlyphSprite> glyphSprites = new Char2ObjectOpenHashMap<>();

    public DefaultFontAtlas(final Font font) {
        this.font = font;
    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public GlyphSprite getGlyphSprite(final char c) {
        return null;
    }

    @Override
    public Char2ObjectMap<GlyphSprite> getGlyphSprites() {
        return Char2ObjectMaps.unmodifiable(glyphSprites);
    }

    @Override
    public void bind() {

    }

    @Override
    public void unbind() {

    }
}
