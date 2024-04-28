/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.color;

import io.karma.pda.api.common.util.RectangleCorner;
import net.minecraft.resources.ResourceLocation;

/**
 * @author Alexander Hinze
 * @since 29/04/2024
 */
public final class DefaultGradientFunction implements GradientFunction {
    private final ResourceLocation name;
    private final AnonGradientFunction function;

    DefaultGradientFunction(final ResourceLocation name, final AnonGradientFunction function) {
        this.name = name;
        this.function = function;
    }

    @Override
    public ResourceLocation getName() {
        return name;
    }

    @Override
    public Color remap(final Color start, final Color end, final RectangleCorner corner) {
        return function.remap(start, end, corner);
    }
}
