/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.flex;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
public interface FlexNode {
    void setFrom(final FlexNode flexNode);

    List<FlexNode> getChildren();

    void addChild(final FlexNode child);

    void removeChild(final FlexNode child);

    int indexOfChild(final FlexNode child);

    @Nullable
    FlexNode getChild(final int index);

    void clearChildren();

    void setDirection(final FlexDirection direction);

    FlexDirection getDirection();

    void setOverflow(final FlexOverflow overflow);

    FlexOverflow getOverflow();

    void setPositionType(final FlexPositionType positionType);

    FlexPositionType getPositionType();

    void setSelfAlignment(final FlexAlignment selfAlignment);

    FlexAlignment getSelfAlignment();

    void setItemAlignment(final FlexAlignment itemAlignment);

    FlexAlignment getItemAlignment();

    void setContentAlignment(final FlexAlignment contentAlignment);

    FlexAlignment getContentAlignment();

    void setContentJustification(final FlexJustify contentJustification);

    FlexJustify getContentJustification();

    void setX(final FlexValue x);

    FlexValue getX();

    void setY(final FlexValue y);

    FlexValue getY();

    void setWidth(final FlexValue width);

    FlexValue getWidth();

    void setHeight(final FlexValue height);

    FlexValue getHeight();

    void setMargin(final FlexBorder margin);

    FlexBorder getMargin();

    void setPadding(final FlexBorder padding);

    FlexBorder getPadding();

    int getAbsoluteWidth();

    int getAbsoluteHeight();

    int getAbsoluteX();

    int getAbsoluteY();

    void computeLayout(final int width, final int height);
}
