/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.theme;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultTypography implements Typography {
    public static final DefaultTypography VANILLA = new DefaultTypography(() -> Minecraft.getInstance().font, 12F);

    private final Supplier<Font> font;
    private final float defaultSize;

    public DefaultTypography(final Supplier<Font> font, final float defaultSize) {
        this.font = font;
        this.defaultSize = defaultSize;
    }

    @Override
    public Font getFont() {
        return font.get();
    }

    @Override
    public float getDefaultSize() {
        return defaultSize;
    }
}
