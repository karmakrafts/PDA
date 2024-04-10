/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.flex;

/**
 * @author Alexander Hinze
 * @since 20/03/2024
 */
public interface FlexValue {
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
        return ZeroFlexValue.AUTO;
    }

    static FlexValue zero() {
        return ZeroFlexValue.INSTANCE;
    }

    FlexValueType getType();

    float get();

    final class ZeroFlexValue implements FlexValue {
        static final ZeroFlexValue INSTANCE = new ZeroFlexValue(FlexValueType.PIXEL);
        static final ZeroFlexValue AUTO = new ZeroFlexValue(FlexValueType.AUTO);

        private final FlexValueType type;

        private ZeroFlexValue(final FlexValueType type) {
            this.type = type;
        }

        @Override
        public FlexValueType getType() {
            return type;
        }

        @Override
        public float get() {
            return 0F;
        }
    }
}
