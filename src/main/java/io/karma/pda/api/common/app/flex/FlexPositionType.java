package io.karma.pda.api.common.app.flex;

import org.lwjgl.util.yoga.Yoga;

/**
 * @author Alexander Hinze
 * @since 20/03/2024
 */
public enum FlexPositionType {
    // @formatter:off
    STATIC  (Yoga.YGPositionTypeStatic),
    RELATIVE(Yoga.YGPositionTypeRelative),
    ABSOLUTE(Yoga.YGPositionTypeAbsolute);
    // @formatter:on

    private final int value;

    FlexPositionType(final int value) {
        this.value = value;
    }

    public static FlexPositionType fromValue(final int value) {
        return switch(value) { // @formatter:off
            case Yoga.YGPositionTypeStatic   -> STATIC;
            case Yoga.YGPositionTypeRelative -> RELATIVE;
            case Yoga.YGPositionTypeAbsolute -> ABSOLUTE;
            default                          -> throw new IllegalArgumentException();
        }; // @formatter:on
    }

    public int getValue() {
        return value;
    }
}
