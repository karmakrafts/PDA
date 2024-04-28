/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.color;

import net.minecraft.resources.ResourceLocation;

/**
 * @author Alexander Hinze
 * @since 28/04/2024
 */
public interface GradientFunction extends AnonGradientFunction {
    static GradientFunction named(final ResourceLocation name, final AnonGradientFunction function) {
        return new DefaultGradientFunction(name, function);
    }

    ResourceLocation getName();
}
