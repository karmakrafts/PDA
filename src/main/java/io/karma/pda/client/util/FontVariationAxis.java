/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 24/05/2024
 */
@OnlyIn(Dist.CLIENT)
public record FontVariationAxis(String name, float min, float max, float def) {
}
