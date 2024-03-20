package io.karma.pda.api.common.app.flex;

import org.lwjgl.util.yoga.Yoga;

/**
 * @author Alexander Hinze
 * @since 20/03/2024
 */
public enum FlexAlignment {
    // @formatter:off
    AUTO            (Yoga.YGAlignAuto),
    FLEX_START      (Yoga.YGAlignFlexStart),
    CENTER          (Yoga.YGAlignCenter),
    FLEX_END        (Yoga.YGAlignFlexEnd),
    STRETCH         (Yoga.YGAlignStretch),
    BASELINE        (Yoga.YGAlignBaseline),
    SPACE_BETWEEN   (Yoga.YGAlignSpaceBetween),
    SPACE_AROUND    (Yoga.YGAlignSpaceAround);
    // @formatter:on

    private final int value;

    FlexAlignment(final int value) {
        this.value = value;
    }

    public static FlexAlignment fromValue(final int value) {
        return switch (value) { // @formatter:off
            case Yoga.YGAlignAuto          -> AUTO;
            case Yoga.YGAlignFlexStart     -> FLEX_START;
            case Yoga.YGAlignCenter        -> CENTER;
            case Yoga.YGAlignFlexEnd       -> FLEX_END;
            case Yoga.YGAlignStretch       -> STRETCH;
            case Yoga.YGAlignBaseline      -> BASELINE;
            case Yoga.YGAlignSpaceBetween  -> SPACE_BETWEEN;
            case Yoga.YGAlignSpaceAround   -> SPACE_AROUND;
            default                        -> throw new IllegalArgumentException();
        }; // @formatter:on
    }

    public int getValue() {
        return value;
    }
}
