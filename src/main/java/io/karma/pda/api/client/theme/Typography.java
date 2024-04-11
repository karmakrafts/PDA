/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.theme;

import net.minecraft.client.gui.Font;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public interface Typography {
    Font getFont();

    float getDefaultSize();
}
