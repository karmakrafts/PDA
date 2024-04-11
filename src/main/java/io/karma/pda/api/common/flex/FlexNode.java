/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.flex;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
public interface FlexNode {
    default FlexDirection getDirection() {
        return FlexDirection.ROW;
    }

    default FlexOverflow getOverflow() {
        return FlexOverflow.HIDDEN;
    }

    default FlexPositionType getPositionType() {
        return FlexPositionType.RELATIVE;
    }

    default FlexAlignment getSelfAlignment() {
        return FlexAlignment.AUTO;
    }

    default FlexAlignment getItemAlignment() {
        return FlexAlignment.AUTO;
    }

    default FlexAlignment getContentAlignment() {
        return FlexAlignment.AUTO;
    }

    default FlexJustify getContentJustification() {
        return FlexJustify.CENTER;
    }

    default FlexValue getX() {
        return FlexValue.auto();
    }

    default FlexValue getY() {
        return FlexValue.auto();
    }

    default FlexValue getWidth() {
        return FlexValue.auto();
    }

    default FlexValue getHeight() {
        return FlexValue.auto();
    }

    default FlexBorder getMargin() {
        return FlexBorder.empty();
    }

    default FlexBorder getPadding() {
        return FlexBorder.empty();
    }
}
