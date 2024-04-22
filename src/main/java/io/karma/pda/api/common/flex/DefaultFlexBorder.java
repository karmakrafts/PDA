/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.flex;

import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 22/04/2024
 */
final class DefaultFlexBorder implements FlexBorder {
    private final FlexValue left;
    private final FlexValue right;
    private final FlexValue top;
    private final FlexValue bottom;

    DefaultFlexBorder(final FlexValue left, final FlexValue right, final FlexValue top, final FlexValue bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FlexBorder border)) {
            return false;
        }
        // @formatter:off
        return left.equals(border.getLeft())
            && right.equals(border.getRight())
            && top.equals(border.getTop())
            && bottom.equals(border.getBottom());
        // @formatter:on
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right, top, bottom);
    }
}
