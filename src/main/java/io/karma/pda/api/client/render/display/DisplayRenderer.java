/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.display;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 04/06/2024
 */
@OnlyIn(Dist.CLIENT)
public interface DisplayRenderer {
    float getGlitchFactor();

    DisplayMode getDisplayMode(final ItemStack stack);
}
