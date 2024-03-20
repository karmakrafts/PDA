package io.karma.pda.api.common.app.flex;

import org.lwjgl.util.yoga.YGValue;
import org.lwjgl.util.yoga.Yoga;

/**
 * @author Alexander Hinze
 * @since 20/03/2024
 */
public interface FlexValue {
    static FlexValue fromStruct(final YGValue value) {
        return switch (value.unit()) { // @formatter:off
            case Yoga.YGUnitPoint   -> pixel((int) value.value());
            case Yoga.YGUnitPercent -> percent(value.value());
            default                 -> auto();
        }; // @formatter:on
    }

    static FlexValue pixel(final int pixels) {
        return new FlexValue() {
            @Override
            public FlexValueType getType() {
                return FlexValueType.PIXEL;
            }

            @Override
            public float get() {
                return pixels;
            }
        };
    }

    static FlexValue percent(final float percent) {
        return new FlexValue() {
            @Override
            public FlexValueType getType() {
                return FlexValueType.PIXEL;
            }

            @Override
            public float get() {
                return percent;
            }
        };
    }

    static FlexValue auto() {
        return new FlexValue() {
            @Override
            public FlexValueType getType() {
                return FlexValueType.AUTO;
            }

            @Override
            public float get() {
                return 0F;
            }
        };
    }

    FlexValueType getType();

    float get();
}
