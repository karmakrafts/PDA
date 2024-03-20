package io.karma.pda.api.common.app.flex;

import org.lwjgl.util.yoga.Yoga;

/**
 * @author Alexander Hinze
 * @since 20/03/2024
 */
public enum FlexOverflow {
    // @formatter:off
    VISIBLE(Yoga.YGOverflowVisible),
    HIDDEN (Yoga.YGOverflowHidden),
    SCROLL (Yoga.YGOverflowScroll);
    // @formatter:on

    private final int value;

    FlexOverflow(final int value) {
        this.value = value;
    }

    public static FlexOverflow fromValue(final int value) {
        return switch (value) { // @formatter:off
            case Yoga.YGOverflowVisible -> VISIBLE;
            case Yoga.YGOverflowHidden  -> HIDDEN;
            case Yoga.YGOverflowScroll  -> SCROLL;
            default                     -> throw new IllegalArgumentException();
        }; // @formatter:on
    }

    public int getValue() {
        return value;
    }
}
