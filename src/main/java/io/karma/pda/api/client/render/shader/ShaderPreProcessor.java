/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 17/06/2024
 */
@OnlyIn(Dist.CLIENT)
public interface ShaderPreProcessor {
    String process(final String resourceName, final String source);
}
