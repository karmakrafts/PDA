/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

import io.karma.pda.api.common.app.theme.font.Font;
import io.karma.pda.api.common.app.theme.font.FontFamily;
import io.karma.pda.api.common.app.theme.font.FontStyle;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.EnumSet;

/**
 * @author Alexander Hinze
 * @since 28/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultFontFamily implements FontFamily {
    @Override
    public ResourceLocation getName() {
        return null;
    }

    @Override
    public EnumSet<FontStyle> getStyles() {
        return null;
    }

    @Override
    public Font getFont(FontStyle style) {
        return null;
    }
}
