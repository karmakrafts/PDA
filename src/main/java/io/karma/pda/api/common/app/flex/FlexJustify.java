package io.karma.pda.api.common.app.flex;

import org.lwjgl.util.yoga.Yoga;

/**
 * @author Alexander Hinze
 * @since 20/03/2024
 */
public enum FlexJustify {
    // @formatter:off
    FLEX_START   (Yoga.YGJustifyFlexStart),
    CENTER       (Yoga.YGJustifyCenter),
    FLEX_END     (Yoga.YGJustifyFlexEnd),
    SPACE_BETWEEN(Yoga.YGJustifySpaceBetween),
    SPACE_AROUND (Yoga.YGJustifySpaceAround),
    SPACE_EVENLY (Yoga.YGJustifySpaceEvenly);
    // @formatter:on

    private final int value;

    FlexJustify(final int value) {
        this.value = value;
    }

    public static FlexJustify fromValue(final int value) {
        return switch (value) {
            case Yoga.YGJustifyFlexStart -> FLEX_START;
            case Yoga.YGJustifyCenter -> CENTER;
            case Yoga.YGJustifyFlexEnd -> FLEX_END;
            case Yoga.YGJustifySpaceBetween -> SPACE_BETWEEN;
            case Yoga.YGJustifySpaceAround -> SPACE_AROUND;
            case Yoga.YGJustifySpaceEvenly -> SPACE_EVENLY;
            default -> throw new IllegalArgumentException();
        };
    }

    public int getValue() {
        return value;
    }
}
