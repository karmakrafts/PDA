package io.karma.pda.api.common.app.flex;

import org.lwjgl.util.yoga.Yoga;

/**
 * @author Alexander Hinze
 * @since 20/03/2024
 */
public enum FlexDirection {
    // @formatter:off
    ROW             (Yoga.YGFlexDirectionRow),
    ROW_REVERSE     (Yoga.YGFlexDirectionRowReverse),
    COLUMN          (Yoga.YGFlexDirectionColumn),
    COLUMN_REVERSE  (Yoga.YGFlexDirectionColumnReverse);
    // @formatter:on

    private final int value;

    FlexDirection(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
