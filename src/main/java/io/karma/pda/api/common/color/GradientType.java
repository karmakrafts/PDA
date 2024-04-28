/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.color;

import io.karma.pda.api.common.util.Constants;
import io.karma.pda.api.common.util.RectangleCorner;
import net.minecraft.resources.ResourceLocation;

/**
 * @author Alexander Hinze
 * @since 28/04/2024
 */
public enum GradientType implements GradientFunction {
    // @formatter:off
    NONE          ((color1, color2, corner) -> color1),
    HORIZONTAL    (GradientType::remapHorizontal),
    VERTICAL      (GradientType::remapVertical),
    ROTATED_45    (GradientType::remapRotated45),
    ROTATED_NEG_45(GradientType::remapRotatedNeg45);
    // @formatter:on

    private final AnonGradientFunction function;
    private final ResourceLocation name;

    GradientType(final AnonGradientFunction function) {
        this.function = function;
        name = new ResourceLocation(Constants.MODID, name().toLowerCase());
    }

    @Override
    public ResourceLocation getName() {
        return name;
    }

    @Override
    public Color remap(final Color start, final Color end, final RectangleCorner corner) {
        return function.remap(start, end, corner);
    }

    private static Color remapHorizontal(final Color start, final Color end, final RectangleCorner corner) {
        return switch (corner) {
            case TOP_LEFT, BOTTOM_LEFT -> start;
            default -> end;
        };
    }

    private static Color remapVertical(final Color start, final Color end, final RectangleCorner corner) {
        return switch (corner) {
            case TOP_LEFT, TOP_RIGHT -> start;
            default -> end;
        };
    }

    private static Color remapRotated45(final Color start, final Color end, final RectangleCorner corner) {
        return switch (corner) {
            case TOP_RIGHT, BOTTOM_LEFT -> start;
            default -> end;
        };
    }

    private static Color remapRotatedNeg45(final Color start, final Color end, final RectangleCorner corner) {
        return switch (corner) {
            case TOP_LEFT, BOTTOM_RIGHT -> start;
            default -> end;
        };
    }
}
