/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

/**
 * @author Alexander Hinze
 * @since 17/06/2024
 */
@OnlyIn(Dist.CLIENT)
public interface ShaderPreProcessor {
    String process(final String source, final ShaderProgram program,
                   final ShaderObject object, final Function<ResourceLocation, String> loader);
}
