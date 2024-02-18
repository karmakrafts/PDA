/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.util;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 07/02/2024
 */
public enum HorizontalDirection implements StringRepresentable {
    // @formatter:off
    NORTH   (Direction.NORTH),
    EAST    (Direction.EAST),
    SOUTH   (Direction.SOUTH),
    WEST    (Direction.WEST);
    // @formatter:on

    private final Direction direction;

    HorizontalDirection(final Direction direction) {
        this.direction = direction;
    }

    public static HorizontalDirection of(final Direction direction) {
        return switch(direction) { // @formatter:off
            case NORTH  -> HorizontalDirection.NORTH;
            case EAST   -> HorizontalDirection.EAST;
            case SOUTH  -> HorizontalDirection.SOUTH;
            case WEST   -> HorizontalDirection.WEST;
            default     -> throw new IllegalStateException("Not a horizontal direction");
        }; // @formatter:on
    }

    @Override
    public @NotNull String getSerializedName() {
        return name().toLowerCase();
    }

    public Direction getDirection() {
        return direction;
    }
}
