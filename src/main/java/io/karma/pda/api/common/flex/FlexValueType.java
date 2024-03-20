package io.karma.pda.api.common.flex;

import org.lwjgl.util.yoga.Yoga;

/**
 * @author Alexander Hinze
 * @since 20/03/2024
 */
public enum FlexValueType {
    // @formatter:off
    PIXEL   (Yoga.YGUnitPoint),
    PERCENT (Yoga.YGUnitPercent),
    AUTO    (Yoga.YGUnitAuto);
    // @formatter:on

    private final int value;

    FlexValueType(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
