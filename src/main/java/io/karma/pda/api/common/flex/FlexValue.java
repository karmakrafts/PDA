/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.flex;

/**
 * @author Alexander Hinze
 * @since 20/03/2024
 */
public interface FlexValue {
    static FlexValue of(final FlexValueType type, final float value) {
        return new DefaultFlexValue(type, value);
    }

    static FlexValue pixel(final int pixels) {
        return new DefaultFlexValue(FlexValueType.PIXEL, pixels);
    }

    static FlexValue percent(final float percent) {
        return new DefaultFlexValue(FlexValueType.PERCENT, percent);
    }

    static FlexValue auto() {
        return ZeroFlexValue.AUTO;
    }

    static FlexValue zero() {
        return ZeroFlexValue.INSTANCE;
    }

    FlexValueType getType();

    float get();
}
