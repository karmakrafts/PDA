/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.display;

import io.karma.pda.api.display.DisplayModeSpec;
import io.karma.pda.api.display.DisplayResolution;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;

/**
 * @author Alexander Hinze
 * @since 04/06/2024
 */
@OnlyIn(Dist.CLIENT)
public interface DisplayRenderer {
    float getGlitchFactor();

    DisplayMode getDisplayMode(final DisplayModeSpec modeSpec);

    Optional<DisplayMode> getDisplayMode(final ItemStack stack);

    Framebuffer getFramebuffer(final DisplayResolution resolution);
}
