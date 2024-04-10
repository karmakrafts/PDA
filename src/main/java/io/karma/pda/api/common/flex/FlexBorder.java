/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.flex;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
public interface FlexBorder {
    static FlexBorder of(final FlexValue left, final FlexValue right, final FlexValue top, final FlexValue bottom) {
        return new FlexBorder() {
            @Override
            public FlexValue getLeft() {
                return left;
            }

            @Override
            public FlexValue getRight() {
                return right;
            }

            @Override
            public FlexValue getTop() {
                return top;
            }

            @Override
            public FlexValue getBottom() {
                return bottom;
            }
        };
    }

    static FlexBorder of(final FlexValue width) {
        return of(width, width, width, width);
    }

    static FlexBorder vertical(final FlexValue width) {
        return of(FlexValue.zero(), FlexValue.zero(), width, width);
    }

    static FlexBorder horizontal(final FlexValue width) {
        return of(width, width, FlexValue.zero(), FlexValue.zero());
    }

    static FlexBorder empty() {
        return of(FlexValue.zero());
    }

    FlexValue getLeft();

    FlexValue getRight();

    FlexValue getTop();

    FlexValue getBottom();
}
